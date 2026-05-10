import os
import re

main_activity_path = r"e:\new manga\app\src\main\java\com\readora\app\MainActivity.kt"
with open(main_activity_path, "r", encoding="utf-8") as f:
    content = f.read()

# Define the components to extract and their destinations
destinations = [
    (r"e:\new manga\app\src\main\java\com\readora\app\ui\home", "HomeScreen.kt", ["HomeScreen", "FeaturedCard", "ShelfCard", "ContinueRow", "LastReadPanel"]),
    (r"e:\new manga\app\src\main\java\com\readora\app\ui\library", "LibraryScreen.kt", ["LibraryScreen", "LibraryStatsPanel", "SavedLocalLibraryRow", "LibraryUpdatePanel", "SavedOnlineLibraryRow", "ChipRow", "ComicPoster"]),
    (r"e:\new manga\app\src\main\java\com\readora\app\ui\discover", "DiscoverScreen.kt", ["DiscoverScreen", "OnlineSearchPanel", "LoadingPanel", "ErrorPanel", "OnlineDiscoveryCard", "DiscoveryCard"]),
    (r"e:\new manga\app\src\main\java\com\readora\app\ui\merge", "MergeScreen.kt", ["MergeLabScreen", "ManualMergeBuilder", "SavedMergeGroupCard", "MergeCard"]),
    (r"e:\new manga\app\src\main\java\com\readora\app\ui\settings", "SettingsScreen.kt", ["SettingsScreen", "BackupCenterPanel", "DatabaseFoundationPanel", "CacheStatsPanel", "DownloadQueuePanel", "SourceManagerSection", "SourceHealthPanel"]),
    (r"e:\new manga\app\src\main\java\com\readora\app\ui\reader", "ReaderScreen.kt", ["ReaderScreen", "LocalReaderScreen", "OnlineReaderScreen"]),
    (r"e:\new manga\app\src\main\java\com\readora\app\ui\details", "DetailsScreen.kt", ["DetailsScreen", "OnlineDetailsScreen"]),
    (r"e:\new manga\app\src\main\java\com\readora\app\ui\components", "CommonComponents.kt", ["Header", "SectionTitle", "PremiumPanel", "formatBytes"])
]

def extract_function(content, func_name):
    # Find the start of the function, which might have annotations like @Composable
    # It might also have private or internal modifiers
    pattern = re.compile(r'(?:@[A-Za-z0-9_]+\s*)*(?:private\s+|internal\s+|public\s+)?fun\s+' + func_name + r'\s*\(')
    match = pattern.search(content)
    if not match:
        return None, content

    start_idx = match.start()
    
    # We need to find the matching closing brace
    # First find the first opening brace after start_idx
    brace_start = content.find('{', start_idx)
    if brace_start == -1:
        # Maybe it's a single expression function like fun x() = ...
        newline = content.find('\n', start_idx)
        return content[start_idx:newline], content[:start_idx] + content[newline:]
        
    brace_count = 0
    end_idx = -1
    for i in range(brace_start, len(content)):
        if content[i] == '{':
            brace_count += 1
        elif content[i] == '}':
            brace_count -= 1
            if brace_count == 0:
                end_idx = i + 1
                break
                
    if end_idx != -1:
        func_content = content[start_idx:end_idx]
        new_content = content[:start_idx] + content[end_idx:]
        return func_content, new_content
        
    return None, content

# Extract the imports and package
package_match = re.search(r'^package .+$', content, re.MULTILINE)
package_decl = package_match.group(0) if package_match else "package com.readora.app"

imports = re.findall(r'^import .+$', content, re.MULTILINE)
imports_str = "\n".join(imports)

# To keep it simple and guarantee it compiles, we just copy ALL imports to every file.
# ProGuard will strip unused imports anyway, and Kotlin compiler ignores unused imports.

remaining_content = content

for dest_dir, filename, func_names in destinations:
    os.makedirs(dest_dir, exist_ok=True)
    
    extracted_funcs = []
    for func in func_names:
        func_code, remaining_content = extract_function(remaining_content, func)
        if func_code:
            extracted_funcs.append(func_code)
        else:
            print(f"Warning: Could not find function {func}")
            
    if extracted_funcs:
        file_content = f"{package_decl}\n\n{imports_str}\n\n" + "\n\n".join(extracted_funcs)
        with open(os.path.join(dest_dir, filename), "w", encoding="utf-8") as f:
            f.write(file_content)
        print(f"Created {os.path.join(dest_dir, filename)} with {len(extracted_funcs)} functions.")

# Write remaining content back to MainActivity.kt
with open(main_activity_path, "w", encoding="utf-8") as f:
    f.write(remaining_content)
print("Updated MainActivity.kt")

