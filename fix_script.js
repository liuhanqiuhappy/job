const fs = require("fs");
const path = require("path");
const base = "D:\\job\\frontend\\src\\views";

// Define fixes for each file
const fixes = {
  "Candidates.vue": {
    // Fix axios -> service
    replace: [
      ["axios.get('/api/match/recommend/candidates', { withCredentials: true })", "service.get('/match/recommend/candidates')"]
    ]
  },
  "Recommend.vue": {},
  "Personal.vue": {},
  "Enterprise.vue": {},
  "IntentManagement.vue": {}
};

Object.entries(fixes).forEach(([name, fix]) => {
  const p = path.join(base, name);
  let content = fs.readFileSync(p, "utf-8");
  
  // 1. Apply any replacements (for Candidates.vue - fix axios -> service)
  if (fix.replace) {
    fix.replace.forEach(([from, to]) => {
      content = content.replace(from, to);
    });
  }
  
  // 2. Add useRouter import after the "from 'vue'" import
  content = content.replace(
    /(import \{[^}]*\} from 'vue')/,
    "$1\nimport { useRouter } from 'vue-router'"
  );
  
  // 3. Find last import line and insert const router = useRouter() after it
  const lines = content.split("\n");
  let lastImportIdx = -1;
  lines.forEach((line, i) => {
    const s = line.trim();
    if (s.startsWith("import ") && s.includes("from ")) {
      lastImportIdx = i;
    }
  });
  if (lastImportIdx >= 0) {
    lines.splice(lastImportIdx + 1, 0, "const router = useRouter()");
    content = lines.join("\n");
  }
  
  fs.writeFileSync(p, content, "utf-8");
  console.log(name + ": Fixed");
});
