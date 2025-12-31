// scripts/jira-from-junit.js
// Node 18+ (có fetch sẵn)

const fs = require("fs");
const path = require("path");
const { parseStringPromise } = require("xml2js");

// Tùy chọn: cho phép set đường dẫn .env bằng ENV_FILE, mặc định ".env"
const envFile = process.env.ENV_FILE || ".env";
const envPath = path.resolve(process.cwd(), envFile);

const dotenv = require("dotenv");
dotenv.config({ path: envPath });

const REPORT_DIR = process.env.JUNIT_DIR || "target/surefire-reports";

// Jira env
const JIRA_BASE_URL = process.env.JIRA_BASE_URL; // https://your-domain.atlassian.net
const JIRA_EMAIL = process.env.JIRA_EMAIL;
const JIRA_API_TOKEN = process.env.JIRA_API_TOKEN;
const JIRA_PROJECT_KEY = process.env.JIRA_PROJECT_KEY; // e.g. "QA"
const JIRA_ISSUE_TYPE = process.env.JIRA_ISSUE_TYPE || "Task"; // or "Bug", "Test", ...

// behavior env
const CREATE_ON = process.env.CREATE_ON || "success"; // success | failure | all
const LABEL = process.env.JIRA_LABEL || "auto-test";

function mustEnv(name, v) {
    if (!v) throw new Error(`Missing env ${name}`);
}

function authHeader() {
    const raw = `${JIRA_EMAIL}:${JIRA_API_TOKEN}`;
    return "Basic " + Buffer.from(raw).toString("base64");
}

function listXmlFiles(dir) {
    if (!fs.existsSync(dir)) return [];
    return fs.readdirSync(dir)
        .filter(f => f.endsWith(".xml"))
        .map(f => path.join(dir, f));
}

function normalizeTestcases(xmlObj) {
    // surefire xml có thể là <testsuite> hoặc <testsuites>
    const suites = [];
    if (xmlObj.testsuite) suites.push(xmlObj.testsuite);
    if (xmlObj.testsuites?.testsuite) suites.push(...xmlObj.testsuites.testsuite);

    const cases = [];
    for (const s of suites) {
        const testcases = s.testcase || [];
        for (const tc of testcases) {
            const name = tc.$?.name || "unknown";
            const classname = tc.$?.classname || "unknown";
            const time = tc.$?.time || "0";
            const failure = tc.failure?.length ? tc.failure[0] : null;
            const error = tc.error?.length ? tc.error[0] : null;
            const skipped = tc.skipped?.length ? tc.skipped[0] : null;

            let status = "passed";
            if (failure || error) status = "failed";
            else if (skipped) status = "skipped";

            cases.push({ name, classname, time, status, failure, error, skipped });
        }
    }
    return cases;
}

function shouldCreate(status) {
    if (CREATE_ON === "all") return true;
    if (CREATE_ON === "failure") return status === "failed";
    if (CREATE_ON === "success") return status === "passed";
    return false;
}

async function createJiraIssue(testcase) {
    const url = `${JIRA_BASE_URL}/rest/api/3/issue`;

    const summary = `[TEST:${testcase.status.toUpperCase()}] ${testcase.classname} :: ${testcase.name}`;

    const details = [
        `*Test class:* ${testcase.classname}`,
        `*Test name:* ${testcase.name}`,
        `*Status:* ${testcase.status}`,
        `*Time:* ${testcase.time}s`,
    ];

    if (testcase.failure) details.push(`*Failure:* ${JSON.stringify(testcase.failure).slice(0, 1500)}`);
    if (testcase.error) details.push(`*Error:* ${JSON.stringify(testcase.error).slice(0, 1500)}`);

    const body = {
        fields: {
            project: { key: JIRA_PROJECT_KEY },
            issuetype: { name: JIRA_ISSUE_TYPE },
            summary,
            description: {
                type: "doc",
                version: 1,
                content: [
                    {
                        type: "paragraph",
                        content: [{ type: "text", text: details.join("\n") }]
                    }
                ]
            },
            labels: [LABEL],
        }
    };

    const res = await fetch(url, {
        method: "POST",
        headers: {
            "Authorization": authHeader(),
            "Accept": "application/json",
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(`Jira create issue failed: ${res.status} ${text}`);
    }
    return res.json();
}

async function main() {
    mustEnv("JIRA_BASE_URL", JIRA_BASE_URL);
    mustEnv("JIRA_EMAIL", JIRA_EMAIL);
    mustEnv("JIRA_API_TOKEN", JIRA_API_TOKEN);
    mustEnv("JIRA_PROJECT_KEY", JIRA_PROJECT_KEY);

    const xmlFiles = listXmlFiles(REPORT_DIR);
    console.log(`Found ${xmlFiles.length} JUnit XML file(s) in ${REPORT_DIR}`);

    let total = 0;
    let created = 0;

    for (const file of xmlFiles) {
        const xml = fs.readFileSync(file, "utf8");
        const obj = await parseStringPromise(xml);
        const testcases = normalizeTestcases(obj);

        for (const tc of testcases) {
            total++;
            if (!shouldCreate(tc.status)) continue;

            // (Tùy chọn) Dedup: nếu muốn tránh tạo trùng, bạn cần search issue theo summary trước.
            const issue = await createJiraIssue(tc);
            created++;
            console.log(`Created: ${issue.key} for ${tc.classname} :: ${tc.name}`);
        }
    }

    console.log(`Done. total testcases=${total}, created issues=${created}`);
}

main().catch(err => {
    console.error(err);
    process.exit(1);
});
