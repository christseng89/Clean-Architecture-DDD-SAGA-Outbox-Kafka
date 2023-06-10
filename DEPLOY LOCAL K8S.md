# Run project in local k8s

1. Git Clone CP Helm Chart
   md food-ordering-system-infra\helm
   cd food-ordering-system-infra\helm
   git clone https://github.com/confluentinc/cp-helm-charts.git
   cd ..\..

   // Update helm\cp-helm-charts\charts\cp-zookeeper\templates\poddisruptionbudget.yaml from v1beta to v1

2. Helm Install CP Kafka Server to K8S
   helm upgrade --install kafka food-ordering-system-infra/helm/cp-helm-charts --version 0.6.0

3. Install Kafka Client
   kubectl apply -f food-ordering-system\k8s-yamls\docker-desktop\kafka-client.yml

4. Create Kafka topics
   kubectl exec -it kafka-client -- bash

   kafka-topics --zookeeper kafka-cp-zookeeper-headless:2181 --topic customer --create --partitions 3 --replication-factor 3 --if-not-exists

   kafka-topics --zookeeper kafka-cp-zookeeper-headless:2181 --topic payment-request --create --partitions 3 --replication-factor 3 --if-not-exists

   kafka-topics --zookeeper kafka-cp-zookeeper-headless:2181 --topic payment-response --create --partitions 3 --replication-factor 3 --if-not-exists

   kafka-topics --zookeeper kafka-cp-zookeeper-headless:2181 --topic restaurant-approval-request --create --partitions 3 --replication-factor 3 --if-not-exists

   kafka-topics --zookeeper kafka-cp-zookeeper-headless:2181 --topic restaurant-approval-response --create --partitions 3 --replication-factor 3 --if-not-exists

   kafka-topics --zookeeper kafka-cp-zookeeper-headless:2181 --list
   ...
   customer
   kafka-cp-kafka-connect-config
   kafka-cp-kafka-connect-offset
   kafka-cp-kafka-connect-status
   payment-request
   payment-response
   restaurant-approval-request
   restaurant-approval-response
   ...

   exit

### Food Ordering System   
1. Install Food Ordering Microservices to k8s
   docker images | grep food

   kubectl apply -f food-ordering-system\k8s-yamls\docker-desktop\application-deployment-local.yml
   kubectl get po | grep deploy
   kubectl get svc | grep service
2. Postman Test (wait for 3 mins for all pods to be ready)
   - New Customer #1
   - New Customer #2
   - Post an Order - APPROVED
   - Tracking Order
   - Post an Order - CANCELLED
   - Tracking Order  

### Kafka-UI
1. Install Kafka-UI to k8s
   git clone https://github.com/provectus/kafka-ui.git

   helm upgrade --install kafka-ui kafka-ui/charts/kafka-ui -f food-ordering-system\k8s-yamls\docker-desktop\kafka-ui-values.yml

   kubectl delete svc kafka-ui
   kubectl apply -f food-ordering-system\k8s-yamls\docker-desktop\kafka-ui-service.yml

2. Access Kafka-UI from Browser
   http://localhost:8100

3. NOTE: Kafka-UI helm install support Ingress...
   --set ingress.enabled= \
   --set ingress.hosts[0].host=kafka-ui.localhost

### Uninstall 
- helm uninstall kafka-ui
- kubectl delete -f food-ordering-system\k8s-yamls\docker-desktop\application-deployment-local.yml
- kubectl delete -f food-ordering-system\k8s-yamls\docker-desktop\kafka-client.yml
- helm uninstall kafka
