
rootProject.name = "interview-planning"

include(
    "internal-api",
    "app",
)
include("app:period-subdomain")
findProject(":app:period-subdomain")?.name = "period-subdomain"
