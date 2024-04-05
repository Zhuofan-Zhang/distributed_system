1. Download and setup development environment with JDK11.
2. Prepare docker and minikube environment following the official guides.
   - https://docs.docker.com/manuals/ <br-
   - https://minikube.sigs.k8s.io/docs/start/
3. To run applications using docker, run:
```shell
chmod +x ./docker.sh
./docker.sh
```
4. To deploy the services with minikube, run the following commands. However, you might encounter severe performance issue:
```shell
chmod +x ./k8s/start_deployments.sh
./k8s/start_deployments.sh
```
5. Exposing servicses deployed using minikube using following commands:
```shell
kubectl port-forward service/distributed-coupon-service 9002:9002
kubectl port-forward service/distributed-order-service 9004:9004
kubectl port-forward service/distributed-product-service 9003:9003
kubectl port-forward service/distributed-user-service 9001:9001
```
5. View deployment results through command lines using `kubectl get all`. View deployment details using minikube dashboard run `minikube dashboard` and open the dashboard using the link in the terminal output.
6. Run chaos test using Chaos mesh:
- Install Chaos Mesh following this guide: https://chaos-mesh.org/docs/quick-start/
- Run `kubectl port-forward -n chaos-mesh svc/chaos-dashboard 2333:2333` and open `http://127.0.0.1:2333/#/dashboard` using browser. After opening the dashboard, you can configure any type of test you want using the dashboard.
8. Before running JMeter test, make sure coupon-service and user-service is available on localhost:9001 and localhost:9002. Run JMeter test:
- Install JMeter from: https://jmeter.apache.org/download_jmeter.cgi
- User terminal to enter the unzipped JMeter bin: `cd /path/to/apache-jmeter-*/bin`
- For Mac, run `sh jmeter`. For Windows, run `jmeter.bat`
- Drag and drop the 'JMeterTest.jmx' in the code base to the JMeter GUI and start test.
