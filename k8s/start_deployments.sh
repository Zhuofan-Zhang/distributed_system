#!/bin/bash

# 部署 YAML 文件
kubectl apply -f coupon-deployment.yaml
kubectl apply -f coupon-service.yaml
kubectl apply -f order-deployment.yaml
kubectl apply -f order-service.yaml
kubectl apply -f product-deployment.yaml
kubectl apply -f product-service.yaml
kubectl apply -f user-deployment.yaml
kubectl apply -f user-service.yaml




