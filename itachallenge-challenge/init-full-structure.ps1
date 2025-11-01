$modules = @{
    "challenge-challenge" = "com/itachallenge/challenge"
    "challenge-submissions" = "com/itachallenge/challenge/submissions"
    "challenge-score" = "com/itachallenge/challenge/score"
}

$mainFolders = @(
    "controller",
    "service",
    "service/consumer",       # only for challenge-score
    "repository",
    "document",
    "dto/request",
    "dto/response",
    "exception",
    "event",
    "event/port",
    "event/contract",         # only for challenge-score
    "event/consumer",         # only for challenge-score
    "event/publisher/inproc"
)

$testFolders = @(
    "controller",
    "service",
    "repository",
    "document",
    "dto/request",
    "dto/response",
    "exception",
    "event/port",
    "event/consumer",
    "event/publisher"
)

$descriptions = @{
    "controller" = "REST controllers of the module."
    "service" = "Business logic of the module."
    "service/consumer" = "Internal event consumers."
    "repository" = "Database interface."
    "document" = "Persistent models (MongoDB, JPA, etc.)."
    "dto/request" = "DTOs for incoming requests."
    "dto/response" = "DTOs for outgoing responses."
    "exception" = "Module-specific exceptions."
    "event" = "Event management of the module."
    "event/port" = "Interfaces for publishing events."
    "event/contract" = "Shared event definitions."
    "event/consumer" = "Listeners for incoming events."
    "event/publisher/inproc" = "Internal event publishers."
}

foreach ($module in $modules.Keys) {
    $baseMain = "$module/src/main/java/$($modules[$module])"
    $baseTest = "$module/src/test/java/$($modules[$module])"

    # Special treatment for challenge-challenge
    if ($module -eq "challenge-challenge") {
        # Only create base directory with .gitkeep and README.md
        New-Item -ItemType Directory -Force -Path $baseMain | Out-Null
        New-Item -ItemType File -Force -Path "$baseMain/.gitkeep" | Out-Null
        $readmePath = "$baseMain/README.md"
        Set-Content -Path $readmePath -Value "# Challenge Module`nMain existing challenge module must be migrated here."

        # Also create basic test structure
        New-Item -ItemType Directory -Force -Path $baseTest | Out-Null
        New-Item -ItemType File -Force -Path "$baseTest/.gitkeep" | Out-Null
        $readmeTestPath = "$baseTest/README.md"
        Set-Content -Path $readmeTestPath -Value "# Challenge Module (test)`nUnit tests for the main existing challenge
        module must be migrated here."

        continue  # Skip to next module
    }

    # For other modules, maintain full structure
    foreach ($folder in $mainFolders) {
        if ($module -ne "challenge-score" -and ($folder -like "*consumer*" -or $folder -like "*contract*")) {
            continue
        }

        $fullPath = "$baseMain/$folder"
        New-Item -ItemType Directory -Force -Path $fullPath | Out-Null
        New-Item -ItemType File -Force -Path "$fullPath/.gitkeep" | Out-Null
        $readmePath = "$fullPath/README.md"
        $folderName = $folder.Split("/")[-1]
        $description = $descriptions[$folder]
        Set-Content -Path $readmePath -Value "# $folderName`n$description"
    }

    # Test structure for modules other than challenge-challenge
    foreach ($folder in $testFolders) {
        if ($module -ne "challenge-score" -and $folder -eq "event/consumer") {
            continue
        }

        $fullPath = "$baseTest/$folder"
        New-Item -ItemType Directory -Force -Path $fullPath | Out-Null
        New-Item -ItemType File -Force -Path "$fullPath/.gitkeep" | Out-Null
        $readmePath = "$fullPath/README.md"
        $folderName = $folder.Split("/")[-1]
        $description = $descriptions[$folder]
        Set-Content -Path $readmePath -Value "# $folderName (test)`n$description for unit testing."
    }
}

Write-Host "Structure fully created. challenge-challenge module simplified with only .gitkeep and README.md."
