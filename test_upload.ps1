$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

$loginBody = '{"username":"test","password":"123"}'
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/login" -Method POST -ContentType "application/json" -Body $loginBody -WebSession $session
Write-Host "Login response: $($loginResponse | ConvertTo-Json)"

$filePath = "d:\test_resume.pdf"
$fileBytes = [System.IO.File]::ReadAllBytes($filePath)

$boundary = [System.Guid]::NewGuid().ToString()
$LF = "`r`n"
$bodyLines = @()
$bodyLines += "--$boundary"
$bodyLines += "Content-Disposition: form-data; name=`"file`"; filename=`"test_resume.pdf`""
$bodyLines += "Content-Type: application/pdf"
$bodyLines += ""
$bodyLines += ""
$bodyLines += "--$boundary"
$bodyLines += "Content-Disposition: form-data; name=`"type`""
$bodyLines += ""
$bodyLines += "resume"
$bodyLines += "--$boundary--"
$bodyLines += ""

$headerPart = [System.Text.Encoding]::UTF8.GetBytes($bodyLines -join $LF)
$footerPart = [System.Text.Encoding]::UTF8.GetBytes($LF + "--$boundary--" + $LF)

$fullBody = New-Object byte[] ($headerPart.Length + $fileBytes.Length + $footerPart.Length)
[System.Array]::Copy($headerPart, 0, $fullBody, 0, $headerPart.Length)
[System.Array]::Copy($fileBytes, 0, $fullBody, $headerPart.Length, $fileBytes.Length)
[System.Array]::Copy($footerPart, 0, $fullBody, $headerPart.Length + $fileBytes.Length, $footerPart.Length)

try {
    $uploadResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/upload" -Method POST -ContentType "multipart/form-data; boundary=$boundary" -Body $fullBody -WebSession $session
    Write-Host "Upload response: $($uploadResponse | ConvertTo-Json)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        Write-Host "Response body: $($reader.ReadToEnd())"
        $reader.Close()
    }
}
