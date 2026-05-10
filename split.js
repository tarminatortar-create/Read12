const fs = require('fs');
const path = require('path');

const mainActivityPath = path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'MainActivity.kt');
let content = fs.readFileSync(mainActivityPath, 'utf-8');

const destinations = [
    [path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'ui', 'home'), 'HomeScreen.kt', ["HomeScreen", "FeaturedCard", "ShelfCard", "ContinueRow", "LastReadPanel"]],
    [path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'ui', 'library'), 'LibraryScreen.kt', ["LibraryScreen", "LibraryStatsPanel", "SavedLocalLibraryRow", "LibraryUpdatePanel", "SavedOnlineLibraryRow", "ChipRow", "ComicPoster"]],
    [path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'ui', 'discover'), 'DiscoverScreen.kt', ["DiscoverScreen", "OnlineSearchPanel", "LoadingPanel", "ErrorPanel", "OnlineDiscoveryCard", "DiscoveryCard"]],
    [path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'ui', 'merge'), 'MergeScreen.kt', ["MergeLabScreen", "ManualMergeBuilder", "SavedMergeGroupCard", "MergeCard"]],
    [path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'ui', 'settings'), 'SettingsScreen.kt', ["SettingsScreen", "BackupCenterPanel", "DatabaseFoundationPanel", "CacheStatsPanel", "DownloadQueuePanel", "SourceManagerSection", "SourceHealthPanel"]],
    [path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'ui', 'reader'), 'ReaderScreen.kt', ["ReaderScreen", "LocalReaderScreen", "OnlineReaderScreen", "ReaderOverlay", "ReaderHeader", "ReaderControls", "TapZoneLayer"]],
    [path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'ui', 'details'), 'DetailsScreen.kt', ["DetailsScreen", "OnlineDetailsScreen", "DetailsHero", "ActionRow", "DescriptionSection", "MergedSourceBadges", "ChapterList"]],
    [path.join('e:', 'new manga', 'app', 'src', 'main', 'java', 'com', 'readora', 'app', 'ui', 'components'), 'CommonComponents.kt', ["Header", "SectionTitle", "PremiumPanel", "formatBytes", "drawAtmosphere", "ErrorBanner", "RetryBanner", "EmptyStatePlaceholder"]]
];

function extractFunction(text, funcName) {
    const regexStr = '(?:@[A-Za-z0-9_]+\\s*)*(?:private\\s+|internal\\s+|public\\s+)?fun\\s+(?:<.*>\\s+)?' + funcName + '\\s*\\(';
    const regex = new RegExp(regexStr);
    const match = regex.exec(text);
    if (!match) return { funcCode: null, newText: text };

    const startIdx = match.index;
    const braceStart = text.indexOf('{', startIdx);
    if (braceStart === -1) {
        // Look for '=' for expression body
        const eqIdx = text.indexOf('=', startIdx);
        if (eqIdx !== -1) {
             const endIdx = text.indexOf('\n', eqIdx);
             if (endIdx !== -1) {
                 return {
                     funcCode: text.substring(startIdx, endIdx),
                     newText: text.substring(0, startIdx) + text.substring(endIdx)
                 };
             }
        }
        return { funcCode: null, newText: text };
    }

    let braceCount = 0;
    let endIdx = -1;
    for (let i = braceStart; i < text.length; i++) {
        if (text[i] === '{') braceCount++;
        else if (text[i] === '}') {
            braceCount--;
            if (braceCount === 0) {
                endIdx = i + 1;
                break;
            }
        }
    }

    if (endIdx !== -1) {
        return {
            funcCode: text.substring(startIdx, endIdx),
            newText: text.substring(0, startIdx) + text.substring(endIdx)
        };
    }
    return { funcCode: null, newText: text };
}

const packageMatch = content.match(/^package .+$/m);
const packageDecl = packageMatch ? packageMatch[0] : "package com.readora.app";

const importsMatch = content.match(/^import .+$/gm);
const importsStr = importsMatch ? importsMatch.join("\n") : "";

let remainingContent = content;

for (const [destDir, filename, funcNames] of destinations) {
    fs.mkdirSync(destDir, { recursive: true });

    let extractedFuncs = [];
    for (const func of funcNames) {
        const { funcCode, newText } = extractFunction(remainingContent, func);
        if (funcCode) {
            extractedFuncs.push(funcCode);
            remainingContent = newText;
        } else {
            console.log(`Warning: Could not find function ${func}`);
        }
    }

    if (extractedFuncs.length > 0) {
        const fileContent = `${packageDecl}\n\n${importsStr}\n\n${extractedFuncs.join("\n\n")}\n`;
        fs.writeFileSync(path.join(destDir, filename), fileContent, 'utf-8');
        console.log(`Created ${path.join(destDir, filename)} with ${extractedFuncs.length} functions.`);
    }
}

fs.writeFileSync(mainActivityPath, remainingContent, 'utf-8');
console.log("Updated MainActivity.kt");
