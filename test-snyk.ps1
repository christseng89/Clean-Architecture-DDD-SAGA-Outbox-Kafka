$env:JAVA_HOME='C:\Users\samfi\.jdks\ms-21.0.11'
$env:Path='C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\plugins\maven\lib\maven3\bin;' + $env:Path
snyk test --all-projects --detection-depth=8 --policy-path=.snyk
