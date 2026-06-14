# Run this after Doxygen to flatten specific sidebar navtree entries.
$file = "docs\navtreedata.js"

$content = Get-Content $file -Raw

# "Packages" -> link directly to com.nvivx.vixhealthsystem page (skips com > nvivx nesting)
$content = $content -replace '("Packages"), "namespaces\.html", \[[\s\S]*?"namespaces_dup"\s*\]\s*\]', '$1, "namespacecom_1_1nvivx_1_1vixhealthsystem.html", null'

# "Class List" keeps "annotated_dup" so classes show organized by package (do NOT replace)

# "Class Members > All" -> direct link (removes a/b/c/d sub-entries)
$content = $content -replace '"functions_dup"', 'null'

# "Class Members > Functions" -> direct link (removes sub-entries if any)
$content = $content -replace '"functions_func"', 'null'

Set-Content $file $content -NoNewline
Write-Host "navtreedata.js patched successfully."
