An application designed for efficiently managing critical information about medications, ensuring users never forget to take them. Key features include:

. secure Authentication: Users can sign in securely using Spring Security and JWT, ensuring a protected and reliable authentication process.
. Validation Across Layers: Medication fields are validated comprehensively, spanning the frontend, backend, and database layers to maintain data integrity.
. Comprehensive CRUD Operations: Users have the ability to delete, edit, and search for medications. Additionally, interactions can be added for each medication, enhancing user control.
. Thorough Testing: The API is tested using Junit 5, Mockito, and integration tests.
. Automated CI/CD: The development workflow is streamlined with GitHub Actions, automating the Continuous Integration process. This ensures that changes are tested and validated before deployment.
. Deployment to AWS: Continuous Deployment is set up to deploy the application to AWS, providing a scalable and reliable hosting environment.
. Containerization: The application is containerized and stored on Docker Hub, facilitating easy deployment and scalability through containerization.

. For api endpoints check controller directory
. login endpoint is /api/v1/auth/login

. check docker-compose to dockerized the app
. Dokerrun v2 json file for EC2 Docker with multiple containers

. A mobile version is planned.
