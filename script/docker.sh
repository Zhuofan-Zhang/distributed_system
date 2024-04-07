aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/l1r4r3i8
cd ..
CURRENT_DIR=$(pwd)

cd $CURRENT_DIR/distributed-order-service
mvn package docker:build
docker tag distributed-order-service:latest public.ecr.aws/l1r4r3i8/distributed-order-service:latest
docker push public.ecr.aws/l1r4r3i8/distributed-order-service:latest

cd $CURRENT_DIR/distributed-coupon-service
mvn package docker:build
docker tag distributed-coupon-service:latest public.ecr.aws/l1r4r3i8/distributed-coupon-service:latest
docker push public.ecr.aws/l1r4r3i8/distributed-coupon-service:latest

cd $CURRENT_DIR/distributed-product-service
mvn package docker:build
docker tag distributed-product-service:latest public.ecr.aws/l1r4r3i8/distributed-product-service:latest
docker push public.ecr.aws/l1r4r3i8/distributed-product-service:latest

cd $CURRENT_DIR/distributed-user-service
mvn package docker:build
docker tag distributed-user-service:latest public.ecr.aws/l1r4r3i8/distributed-user-service:latest
docker push public.ecr.aws/l1r4r3i8/distributed-user-service:latest

#docker pull public.ecr.aws/l1r4r3i8/distributed-order-service:latest
#docker pull public.ecr.aws/l1r4r3i8/distributed-coupon-service:latest
#docker pull public.ecr.aws/l1r4r3i8/distributed-product-service:latest
#docker pull public.ecr.aws/l1r4r3i8/distributed-user-service:latest
#docker run --rm -d -p 9004:9004 public.ecr.aws/l1r4r3i8/distributed-order-service:latest
#docker run --rm -d -p 9002:9002 public.ecr.aws/l1r4r3i8/distributed-coupon-service:latest
#docker run --rm -d -p 9003:9003 public.ecr.aws/l1r4r3i8/distributed-product-service:latest
#docker run --rm -d -p 9001:9001 public.ecr.aws/l1r4r3i8/distributed-user-service:latest
