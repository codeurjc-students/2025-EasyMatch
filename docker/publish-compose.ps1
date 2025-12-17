$DockerUser = "dmunozm5"
$ImageName = "easymatch"
$Tag = "0.1"
$File = "docker-compose.yml"

$FullImageName = "${DockerUser}/${ImageName}-compose:${Tag}"

Write-Host "Publishing $ImageName as OCI Artifact to $FullImageName"

docker compose -f ${File} publish ${FullImageName} --with-env

Write-Host "Published successfully!" -ForegroundColor Green