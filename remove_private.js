const fs = require('fs');
const path = require('path');

function removePrivateMod(dir) {
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            removePrivateMod(fullPath);
        } else if (fullPath.endsWith('.kt')) {
            let content = fs.readFileSync(fullPath, 'utf-8');
            
            // Remove private from top level declarations
            // This regex matches `private ` at the beginning of a line or after annotations
            // e.g., @Composable\nprivate fun
            content = content.replace(/^(\s*(?:@[A-Za-z0-9_]+\s*)*)private\s+(fun|val|var|class|data class|enum class)/gm, '$1$2');
            
            fs.writeFileSync(fullPath, content, 'utf-8');
            console.log(`Removed private modifiers from ${fullPath}`);
        }
    }
}

removePrivateMod(path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'ui'));
removePrivateMod(path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app')); // for MainActivity.kt
