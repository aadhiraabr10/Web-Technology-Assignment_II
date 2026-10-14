param(
    [string]$TomcatWebapps = "C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps"
)

# deploy.ps1 – copies StudentProfilePortal.war to Tomcat webapps
# Run from the project root: .\deploy.ps1
# Or specify a custom Tomcat path: .\deploy.ps1 -TomcatWebapps "D:\Tomcat\webapps"

$war = ".\target\StudentProfilePortal.war"

if (-not (Test-Path $war)) {
    Write-Error "WAR not found. Run: & 'C:\tools\maven\apache-maven-3.9.6\bin\mvn.cmd' clean package"
    exit 1
}

if (-not (Test-Path $TomcatWebapps)) {
    Write-Warning "Tomcat webapps folder not found at: $TomcatWebapps"
    Write-Warning "Edit this script or pass -TomcatWebapps with your actual Tomcat path."
    exit 1
}

Copy-Item $war $TomcatWebapps -Force
Write-Host "Deployed StudentProfilePortal.war to $TomcatWebapps"
Write-Host ""
Write-Host "Now start/restart Tomcat, then open:"
Write-Host "  http://localhost:8080/StudentProfilePortal/"
Write-Host ""
Write-Host "Login with:  admin / admin123   or   teacher / teach456"
