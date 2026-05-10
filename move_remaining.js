const fs = require('fs');
const path = require('path');

const mainActivityPath = path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'MainActivity.kt');
const commonComponentsPath = path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'ui', 'components', 'CommonComponents.kt');

let mainContent = fs.readFileSync(mainActivityPath, 'utf-8');
let commonContent = fs.readFileSync(commonComponentsPath, 'utf-8');

// Find the end of ReadoraBottomBar
const bottomBarRegex = /private fun ReadoraBottomBar[\s\S]*?^}/m;
const match = bottomBarRegex.exec(mainContent);

if (match) {
    const endIdx = match.index + match[0].length;
    
    // Everything after ReadoraBottomBar
    const remaining = mainContent.substring(endIdx);
    
    // Append to CommonComponents
    fs.writeFileSync(commonComponentsPath, commonContent + "\n" + remaining, 'utf-8');
    
    // Truncate MainActivity
    fs.writeFileSync(mainActivityPath, mainContent.substring(0, endIdx) + "\n", 'utf-8');
    
    console.log("Moved remaining code to CommonComponents.kt");
} else {
    console.log("Could not find ReadoraBottomBar");
}
