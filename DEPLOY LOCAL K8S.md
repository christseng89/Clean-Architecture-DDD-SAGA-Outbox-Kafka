# Run project in local k8s

1. Git Clone CP Helm Chart
   md food-ordering-system-infra\helm
   cd food-ordering-system-infra\helm
   git clone https://github.com/confluentinc/cp-helm-charts.git

   // Update helm\cp-helm-charts\charts\cp-zookeeper\templates\poddisruptionbudget.yaml from v1beta to v1

2. Helm Install CP Kafka Server to K8S
   helm upgrade --install kafka food-ordering-system-infra/helm/cp-helm-charts --version 0.6.0

   ...
   To connect from a client pod:

   a. Deploy a kafka client pod with configuration:

      apiVersion: v1
      kind: Pod
      metadata:
         name: kafka-client
         namespace: default
      spec:
         containers:
         - name: kafka-client
         image: confluentinc/cp-enterprise-kafka:6.1.0
         command:
            - sh
            - -c
            - "exec tail -f /dev/null"

   b. Log into the Pod

   kubectl exec -it kafka-client -- /bin/bash

   c. Explore with kafka commands:

   # Create the topic
   kafka-topics --zookeeper kafka-cp-zookeeper-headless:2181 --topic kafka-topic --create --partitions 1 --replication-factor 1 --if-not-exists
   ...

3. Install Kafka Client
   cd ..\..
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

5. Install Food Ordering Microservices to k8s
   docker images | grep food

   kubectl apply -f food-ordering-system\k8s-yamls\docker-desktop\application-deployment-local.yml
   kubectl get po | grep deploy
   kubectl get svc | grep service
6. Postman Test
   - New Customer #1
   - New Customer #2
   - Post an Order - APPROVED
   - Tracking Order
   - Post an Order - CANCELLED
   - Tracking Order  
 