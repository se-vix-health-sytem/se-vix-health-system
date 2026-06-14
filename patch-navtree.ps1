# Run this from the project root after every Doxygen regeneration.

$navtreeFile   = "docs\navtreedata.js"
$annotatedFile = "docs\annotated_dup.js"

# -- navtreedata.js --

$nav = Get-Content $navtreeFile -Raw

# Remove "Class Index" entry (A-Z letter tabs, confusing)
$nav = $nav -replace '\[ "Class Index", "classes\.html", null \],\s*', ''

# Remove "Class Hierarchy" entry
$nav = $nav -replace '\[ "Class Hierarchy", "hierarchy\.html", "hierarchy" \],\s*', ''

# Remove alphabetical sub-entries from Class Members > All and > Functions
$nav = $nav -replace '"functions_dup"', 'null'
$nav = $nav -replace '"functions_func"', 'null'

Set-Content $navtreeFile $nav -NoNewline -Encoding UTF8
Write-Host "navtreedata.js patched."

# -- annotated_dup.js --
# Replace every namespace page URL with null so package folder labels in the
# Class List tree are expand-only (clicking them does nothing, sidebar stays open).
# Class entries keep their URLs and remain fully clickable.

$ann = Get-Content $annotatedFile -Raw
$ann = $ann -replace '"namespacevixhealthsystem[^"]*\.html"', 'null'
Set-Content $annotatedFile $ann -NoNewline -Encoding UTF8
Write-Host "annotated_dup.js patched - package folders are now expand-only in Class List."
