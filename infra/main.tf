terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
}

provider "aws" {
  region = var.region
}




# ---------------- VPC ----------------
resource "aws_vpc" "vpc_1" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = { Name = "${var.prefix}-vpc" }
}




# ---------------- Elastic IP ----------------
resource "aws_eip" "nat_eip" {
  domain = "vpc"
  tags   = { Name = "${var.prefix}-nat-eip" }
}

resource "aws_eip" "haproxy_eip" {
  domain = "vpc"
  tags   = { Name = "${var.prefix}-haproxy-eip" }
}

resource "aws_eip_association" "haproxy_assoc" {
  instance_id   = aws_instance.haproxy.id
  allocation_id = aws_eip.haproxy_eip.id
}



# ---------------- NAT Gateway ----------------
resource "aws_nat_gateway" "nat_gw" {
  allocation_id = aws_eip.nat_eip.id
  subnet_id     = aws_subnet.subnet_public.id
  tags          = { Name = "${var.prefix}-nat-gw" }
}




# ---------------- Subnets ----------------
resource "aws_subnet" "subnet_public" {
  vpc_id                  = aws_vpc.vpc_1.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "${var.region}a"
  map_public_ip_on_launch = true
  tags = { Name = "${var.prefix}-subnet-public" }
}

resource "aws_subnet" "subnet_blue" {
  vpc_id                  = aws_vpc.vpc_1.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "${var.region}b"
  map_public_ip_on_launch = false
  tags = { Name = "${var.prefix}-subnet-blue" }
}

resource "aws_subnet" "subnet_green" {
  vpc_id                  = aws_vpc.vpc_1.id
  cidr_block              = "10.0.3.0/24"
  availability_zone       = "${var.region}c"
  map_public_ip_on_launch = false
  tags = { Name = "${var.prefix}-subnet-green" }
}

resource "aws_subnet" "subnet_redis_sentinel" {
  vpc_id            = aws_vpc.vpc_1.id
  cidr_block        = "10.0.4.0/24"
  availability_zone = "${var.region}d"
  map_public_ip_on_launch = false
  tags = { Name = "${var.prefix}-subnet-redis-sentinel" }
}

resource "aws_subnet" "subnet_redis_mq" {
  vpc_id            = aws_vpc.vpc_1.id
  cidr_block        = "10.0.5.0/24"
  availability_zone = "${var.region}a"
  map_public_ip_on_launch = false
  tags = { Name = "${var.prefix}-subnet-redis-mq" }
}

resource "aws_subnet" "subnet_db" {
  vpc_id            = aws_vpc.vpc_1.id
  cidr_block        = "10.0.6.0/24"
  availability_zone = "${var.region}b"
  map_public_ip_on_launch = false
  tags = { Name = "${var.prefix}-subnet-db" }
}

resource "aws_subnet" "subnet_monitoring" {
  vpc_id            = aws_vpc.vpc_1.id
  cidr_block        = "10.0.7.0/24"
  availability_zone = "${var.region}c"
  map_public_ip_on_launch = false
  tags = { Name = "${var.prefix}-subnet-monitoring" }
}




# ---------------- Route Tables ----------------

# Public Route Table (IGW)
resource "aws_internet_gateway" "igw_1" {
  vpc_id = aws_vpc.vpc_1.id
  tags   = { Name = "${var.prefix}-igw" }
}

resource "aws_route_table" "rt_public" {
  vpc_id = aws_vpc.vpc_1.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw_1.id
  }
  tags = { Name = "${var.prefix}-rt-public" }
}

resource "aws_route_table_association" "assoc_public" {
  subnet_id      = aws_subnet.subnet_public.id
  route_table_id = aws_route_table.rt_public.id
}




# Private Route Table (NAT)
resource "aws_route_table" "rt_private" {
  vpc_id = aws_vpc.vpc_1.id
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat_gw.id
  }
  tags = { Name = "${var.prefix}-rt-private" }
}

resource "aws_route_table_association" "assoc_blue" {
  subnet_id      = aws_subnet.subnet_blue.id
  route_table_id = aws_route_table.rt_private.id
}

resource "aws_route_table_association" "assoc_green" {
  subnet_id      = aws_subnet.subnet_green.id
  route_table_id = aws_route_table.rt_private.id
}

resource "aws_route_table_association" "assoc_redis_sentinel" {
  subnet_id      = aws_subnet.subnet_redis_sentinel.id
  route_table_id = aws_route_table.rt_private.id
}

resource "aws_route_table_association" "assoc_redis_mq" {
  subnet_id      = aws_subnet.subnet_redis_mq.id
  route_table_id = aws_route_table.rt_private.id
}

resource "aws_route_table_association" "assoc_db" {
  subnet_id      = aws_subnet.subnet_db.id
  route_table_id = aws_route_table.rt_private.id
}

resource "aws_route_table_association" "assoc_monitoring" {
  subnet_id      = aws_subnet.subnet_monitoring.id
  route_table_id = aws_route_table.rt_private.id
}




# ---------------- Security Group ----------------
resource "aws_security_group" "sg_1" {
  name   = "${var.prefix}-sg"
  vpc_id = aws_vpc.vpc_1.id

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.prefix}-sg" }
}




# ---------------- IAM Role ----------------
resource "aws_iam_role" "ec2_role" {
  name = "${var.prefix}-ec2-role"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": { "Service": "ec2.amazonaws.com" },
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "s3_full_access" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}

resource "aws_iam_role_policy_attachment" "ec2_ssm" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_role_policy_attachment" "route53_access" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonRoute53FullAccess"
}

resource "aws_iam_instance_profile" "instance_profile" {
  name = "${var.prefix}-instance-profile"
  role = aws_iam_role.ec2_role.name
}




# ---------------- Route53 Private Hosted Zone ----------------
resource "aws_route53_zone" "private" {
  name = "internal.local" # 네트워크 내부에서만 쓰는 도메인
  vpc {
    vpc_id = aws_vpc.vpc_1.id
  }
  comment = "Private Hosted Zone for internal services"
}

# Spring Blue
resource "aws_route53_record" "spring_blue" {
  zone_id = aws_route53_zone.private.zone_id
  name    = "spring-blue.internal.local"
  type    = "A"
  ttl     = 60
  records = [aws_instance.app_blue.private_ip]
}

# Spring Green
resource "aws_route53_record" "spring_green" {
  zone_id = aws_route53_zone.private.zone_id
  name    = "spring-green.internal.local"
  type    = "A"
  ttl     = 60
  records = [aws_instance.app_green.private_ip]
}

# Redis Sentinel Master
resource "aws_route53_record" "redis_master" {
  zone_id = aws_route53_zone.private.zone_id
  name    = "redis-master.internal.local"
  type    = "A"
  ttl     = 60
  records = [aws_instance.redis_master.private_ip]

  lifecycle {
    ignore_changes = [records]
  }
}


# Redis Sentinel Slave1
resource "aws_route53_record" "redis_slave1" {
  zone_id = aws_route53_zone.private.zone_id
  name    = "redis-slave1.internal.local"
  type    = "A"
  ttl     = 60
  records = [aws_instance.redis_slave1.private_ip]
}

# Redis Sentinel Slave2
resource "aws_route53_record" "redis_slave2" {
  zone_id = aws_route53_zone.private.zone_id
  name    = "redis-slave2.internal.local"
  type    = "A"
  ttl     = 60
  records = [aws_instance.redis_slave2.private_ip]
}

# ProxySQL
resource "aws_route53_record" "proxysql" {
  zone_id = aws_route53_zone.private.zone_id
  name    = "proxysql.internal.local"
  type    = "A"
  ttl     = 60
  records = [aws_instance.proxysql.private_ip]
}

# Redis Cache
resource "aws_route53_record" "redis_cache" {
  zone_id = aws_route53_zone.private.zone_id
  name    = "redis-cache.internal.local"
  type    = "A"
  ttl     = 60
  records = [aws_instance.redis_cache.private_ip]
}

# RabbitMQ
resource "aws_route53_record" "rabbitmq" {
  zone_id = aws_route53_zone.private.zone_id
  name    = "rabbitmq.internal.local"
  type    = "A"
  ttl     = 60
  records = [aws_instance.rabbitmq.private_ip]
}

# monitor
resource "aws_route53_record" "monitoring" {
  zone_id = aws_route53_zone.private.zone_id
  name    = "monitoring.internal.local"
  type    = "A"
  ttl     = 60
  records = [aws_instance.monitoring.private_ip]
}



# ---------------- User Data ----------------
locals {
  common_user_data = <<-EOF
#!/bin/bash
# Swap 4GB
dd if=/dev/zero of=/swapfile bs=128M count=32
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo "/swapfile swap swap defaults 0 0" >> /etc/fstab

# 기본 툴 설치 (Amazon Linux 2 → yum 사용)
yum install -y docker awscli bind-utils curl git
systemctl enable docker
systemctl start docker

# ec2-user도 docker 실행 가능하게 권한 부여
usermod -aG docker ec2-user

# 공통 Docker 네트워크 생성 (이미 있으면 무시)
docker network create common || true

# Fluent Bit 설치
curl -s https://packages.fluentbit.io/install.sh | sh
systemctl enable fluent-bit
systemctl start fluent-bit

# EC2 메타데이터/태그 읽기
TOKEN=$(curl -X PUT "http://169.254.169.254/latest/api/token" \
  -H "X-aws-ec2-metadata-token-ttl-seconds: 21600")
INSTANCE_ID=$(curl -H "X-aws-ec2-metadata-token: $TOKEN" \
  -s http://169.254.169.254/latest/meta-data/instance-id)
HOSTNAME=$(curl -H "X-aws-ec2-metadata-token: $TOKEN" \
  -s http://169.254.169.254/latest/meta-data/local-hostname)

# Terraform 태그 메타데이터에서 Role 가져오기
ROLE=$(curl -H "X-aws-ec2-metadata-token: $TOKEN" \
  -s http://169.254.169.254/latest/meta-data/tags/instance/Role)

# Fluent Bit 설정
cat <<EOC > /etc/fluent-bit/fluent-bit.conf
[SERVICE]
    Flush        1
    Daemon       Off
    Log_Level    info
    Parsers_File /etc/fluent-bit/parsers.conf

[INPUT]
    Name        tail
    Path        /var/lib/docker/containers/*/*.log
    Parser      docker
    Tag         docker.$${HOSTNAME}.*
    Docker_Mode On
    Mem_Buf_Limit 50MB
    Skip_Long_Lines On

[FILTER]
    Name        modify
    Match       docker.*
    Add         instance_id  $${INSTANCE_ID}
    Add         role         $${ROLE}
    Add         host         $${HOSTNAME}

[OUTPUT]
    Name        loki
    Match       docker.*
    Host        monitoring.internal.local
    Port        3100
    Labels      job=$${ROLE},instance=$${INSTANCE_ID},host=$${HOSTNAME},container=$${container_name},image=$${image}
EOC

# Fluent Bit 시작
systemctl restart fluent-bit
EOF


  haproxy_user_data = <<-EOF
#!/bin/bash
${local.common_user_data}

mkdir -p /dockerProjects/ha_proxy/volumes/usr/local/etc/haproxy

# HAProxy 설정
cat << 'EOC' > /dockerProjects/ha_proxy/volumes/usr/local/etc/haproxy/haproxy.cfg
global
    log stdout format raw local0

defaults
    mode http
    timeout connect 5s
    timeout client  6h
    timeout server  6h
    timeout http-keep-alive 1m
    option http-keep-alive

frontend http_front
    bind *:80
    option forwardfor
    http-request set-header X-Forwarded-Proto https
    default_backend spring_back

backend spring_back
    option httpchk GET /actuator/health
    server app_blue ${aws_instance.app_blue.private_ip}:8080 check weight 100
    server app_green ${aws_instance.app_green.private_ip}:8080 check backup
EOC

# HAProxy: 내부(common) 네트워크에서만 리슨
docker run -d \
  --name haproxy \
  --restart unless-stopped \
  --network common \
  --network-alias haproxy \
  -v /dockerProjects/ha_proxy/volumes/usr/local/etc/haproxy:/usr/local/etc/haproxy \
  haproxy:latest

# Nginx Proxy Manager: 외부 80/443/81 노출, 백엔드는 http://haproxy:80 로 프록시 설정
docker run -d \
  --name npm \
  --restart unless-stopped \
  --network common \
  -p 80:80 -p 443:443 -p 81:81 \
  -v npm_data:/data \
  -v npm_letsencrypt:/etc/letsencrypt \
  jc21/nginx-proxy-manager:latest
EOF


  app_user_data = <<-EOF
#!/bin/bash
${local.common_user_data}

# EC2는 Docker만 준비, Spring 컨테이너는 GitHub Actions에서 배포
echo "App server ready for Docker deployment"
EOF

  # ---------------- Redis Sentinel User Data ----------------
  redis_master_user_data = <<-EOF
#!/bin/bash
${local.common_user_data}

# Redis Master
docker run -d \
  --name redis-master \
  --restart unless-stopped \
  --network host \
  -v /data/redis/master:/data \
  redis:7 redis-server --requirepass "${var.password_1}" --appendonly yes

# Sentinel + Route53 Failover 업데이트 스크립트 준비
mkdir -p /dockerProjects/redis_sentinel

# Route53 갱신 스크립트
cat << 'EOS' > /dockerProjects/redis_sentinel/update_route53.sh
#!/bin/bash
HOSTED_ZONE_ID="${aws_route53_zone.private.zone_id}"
RECORD_NAME="redis-master.internal.local"
TTL=60

# Redis Sentinel client-reconfig-script 인자:
# $1 = sentinel ip:port
# $2 = old master ip:port
# $3 = new master ip:port
# 일부 환경에서는 $4에 ip만 오는 경우도 있음

NEW_MASTER_ARG="$3"
if [[ -z "$NEW_MASTER_ARG" && -n "$4" ]]; then
  NEW_MASTER_ARG="$4"
fi

# ip:port → ip만 추출
if [[ "$NEW_MASTER_ARG" == *:* ]]; then
  NEW_MASTER_IP=$(echo "$NEW_MASTER_ARG" | cut -d: -f1)
else
  NEW_MASTER_IP="$NEW_MASTER_ARG"
fi

if [ -z "$NEW_MASTER_IP" ]; then
  echo "No new master IP detected, exiting..."
  exit 1
fi

echo "Updating Route53 record: $RECORD_NAME -> $NEW_MASTER_IP"

cat > /tmp/route53-change.json <<EOC
{
  "Comment": "Update redis-master record after failover",
  "Changes": [
    {
      "Action": "UPSERT",
      "ResourceRecordSet": {
        "Name": "$RECORD_NAME",
        "Type": "A",
        "TTL": $TTL,
        "ResourceRecords": [{ "Value": "$NEW_MASTER_IP" }]
      }
    }
  ]
}
EOC

aws route53 change-resource-record-sets \
  --hosted-zone-id $HOSTED_ZONE_ID \
  --change-batch file:///tmp/route53-change.json
EOS

chmod +x /dockerProjects/redis_sentinel/update_route53.sh


# Sentinel 설정 파일
cat << 'EOC' > /dockerProjects/redis_sentinel/sentinel.conf
port 26379
dir /tmp
sentinel monitor mymaster redis-master.internal.local 6379 2
sentinel auth-pass mymaster ${var.password_1}
sentinel down-after-milliseconds mymaster 10000   # 10초 동안 응답없으면 다운 판정
sentinel failover-timeout mymaster 60000          # 60초 내 새 마스터 선출
sentinel parallel-syncs mymaster 1
sentinel client-reconfig-script mymaster /etc/redis/update_route53.sh
EOC

# Sentinel 컨테이너 실행 (awscli 설치 포함)
docker run -d \
  --name redis-sentinel \
  --restart unless-stopped \
  --network host \
  -v /dockerProjects/redis_sentinel:/etc/redis \
  --user root \
  redis:7 sh -lc "apt-get update && apt-get install -y awscli && exec redis-sentinel /etc/redis/sentinel.conf"
EOF


  redis_slave_user_data = <<-EOF
#!/bin/bash
${local.common_user_data}

# Redis Slave (Replica) - 마스터 붙을 때까지 재시도
until docker run -d \
  --name redis-slave \
  --restart unless-stopped \
  --network host \
  -v /data/redis/slave:/data \
  redis:7 redis-server --replicaof redis-master.internal.local 6379 --masterauth "${var.password_1}" --appendonly yes; do
  echo "Retrying to start redis-slave..."
  sleep 5
done

# Sentinel + Route53 Failover 업데이트 스크립트 준비
mkdir -p /dockerProjects/redis_sentinel

cat << 'EOS' > /dockerProjects/redis_sentinel/update_route53.sh
#!/bin/bash
HOSTED_ZONE_ID="${aws_route53_zone.private.zone_id}"
RECORD_NAME="redis-master.internal.local"
TTL=60

# Redis Sentinel client-reconfig-script 인자:
# $1 = sentinel ip:port
# $2 = old master ip:port
# $3 = new master ip:port
# 일부 환경에서는 $4에 ip만 오는 경우도 있음

NEW_MASTER_ARG="$3"
if [[ -z "$NEW_MASTER_ARG" && -n "$4" ]]; then
  NEW_MASTER_ARG="$4"
fi

# ip:port → ip만 추출
if [[ "$NEW_MASTER_ARG" == *:* ]]; then
  NEW_MASTER_IP=$(echo "$NEW_MASTER_ARG" | cut -d: -f1)
else
  NEW_MASTER_IP="$NEW_MASTER_ARG"
fi

if [ -z "$NEW_MASTER_IP" ]; then
  echo "No new master IP detected, exiting..."
  exit 1
fi

echo "Updating Route53 record: $RECORD_NAME -> $NEW_MASTER_IP"

cat > /tmp/route53-change.json <<EOC
{
  "Comment": "Update redis-master record after failover",
  "Changes": [
    {
      "Action": "UPSERT",
      "ResourceRecordSet": {
        "Name": "$RECORD_NAME",
        "Type": "A",
        "TTL": $TTL,
        "ResourceRecords": [{ "Value": "$NEW_MASTER_IP" }]
      }
    }
  ]
}
EOC

aws route53 change-resource-record-sets \
  --hosted-zone-id $HOSTED_ZONE_ID \
  --change-batch file:///tmp/route53-change.json
EOS

chmod +x /dockerProjects/redis_sentinel/update_route53.sh

# Sentinel 설정 파일
cat << 'EOC' > /dockerProjects/redis_sentinel/sentinel.conf
port 26379
dir /tmp
sentinel monitor mymaster redis-master.internal.local 6379 2
sentinel auth-pass mymaster ${var.password_1}
sentinel down-after-milliseconds mymaster 3000
sentinel failover-timeout mymaster 10000
sentinel parallel-syncs mymaster 1
sentinel client-reconfig-script mymaster /etc/redis/update_route53.sh
EOC

# Sentinel 컨테이너 실행 (awscli 설치 포함)
docker run -d \
  --name redis-sentinel \
  --restart unless-stopped \
  --network host \
  -v /dockerProjects/redis_sentinel:/etc/redis \
  --user root \
  redis:7 sh -lc "apt-get update && apt-get install -y awscli && exec redis-sentinel /etc/redis/sentinel.conf"
EOF




  # ---------------- Redis Cache User Data ----------------
  redis_cache_user_data = <<-EOF
#!/bin/bash
${local.common_user_data}

# Redis (Cache Node, Single Instance)
docker run -d \
  --name redis-cache \
  --restart unless-stopped \
  --network host \
  -v /data/redis/cache:/data \
  redis:7 redis-server --requirepass "${var.password_1}" --appendonly yes
EOF


  # ---------------- RabbitMQ User Data ----------------
  rabbitmq_user_data = <<-EOF
#!/bin/bash
${local.common_user_data}

# RabbitMQ (with Management UI)
docker run -d \
  --name rabbitmq \
  --restart unless-stopped \
  --network host \
  -e RABBITMQ_DEFAULT_USER=admin \
  -e RABBITMQ_DEFAULT_PASS="${var.password_1}" \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management
EOF

  # ---------------- MySQL Master ----------------
  mysql_master_user_data = <<-EOF
#!/bin/bash
${local.common_user_data}

# MySQL Master 실행
docker run -d \
  --name mysql-master \
  --restart unless-stopped \
  --network host \
  -e MYSQL_ROOT_PASSWORD=${var.password_1} \
  -v /data/mysql/master:/var/lib/mysql \
  -p 3306:3306 \
  mysql:8.0 \
    --server-id=1 \
    --log-bin=mysql-bin \
    --binlog-format=ROW \
    --gtid_mode=ON \
    --enforce_gtid_consistency=ON

# MySQL 준비될 때까지 대기
until docker exec mysql-master mysqladmin ping -uroot -p${var.password_1} --silent; do
  echo "waiting mysql-master..."
  sleep 5
done

# 복제 계정 및 ProxySQL용 계정 생성
docker exec mysql-master mysql -uroot -p${var.password_1} -e "
  CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED BY '${var.password_1}';
  GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';

  CREATE USER IF NOT EXISTS '${var.db_user}'@'%' IDENTIFIED BY '${var.password_1}';
  GRANT SELECT, INSERT, UPDATE, DELETE ON *.* TO '${var.db_user}'@'%';

  CREATE USER IF NOT EXISTS 'monitor'@'%' IDENTIFIED BY '${var.password_1}';
  GRANT REPLICATION CLIENT ON *.* TO 'monitor'@'%';

  FLUSH PRIVILEGES;
"
EOF


  # ---------------- MySQL Slave ----------------
  mysql_slave1_user_data = <<-EOF
#!/bin/bash
${local.common_user_data}

# MySQL Slave1 실행
docker run -d \
  --name mysql-slave1 \
  --restart unless-stopped \
  --network host \
  -e MYSQL_ROOT_PASSWORD=${var.password_1} \
  -v /data/mysql/slave1:/var/lib/mysql \
  -p 3306:3306 \
  mysql:8.0 \
    --server-id=2 \
    --relay-log=relay-bin \
    --read-only=1 \
    --gtid_mode=ON \
    --enforce_gtid_consistency=ON \
    --super_read_only=1

# Slave MySQL 준비될 때까지 대기
until docker exec mysql-slave1 mysqladmin ping -uroot -p${var.password_1} --silent; do
  echo "waiting mysql-slave1..."
  sleep 5
done

# Master 연결
docker exec mysql-slave1 mysql -uroot -p${var.password_1} -e "
  CHANGE MASTER TO
    MASTER_HOST='${aws_instance.mysql_master.private_ip}',
    MASTER_USER='repl',
    MASTER_PASSWORD='${var.password_1}',
    MASTER_PORT=3306,
    MASTER_AUTO_POSITION=1;
  START SLAVE;
"
EOF

  # ---------------- ProxySQL ----------------
  proxysql_user_data = <<-EOF
#!/bin/bash
${local.common_user_data}

# ProxySQL 설치 디렉토리 준비
mkdir -p /db/proxysql/data /db/proxysql/conf
chmod 777 /db/proxysql /db/proxysql/data /db/proxysql/conf

# ProxySQL 설정 파일 생성
cat << 'EOC' > /db/proxysql/conf/proxysql.cnf
datadir="/var/lib/proxysql"

admin_variables =
{
    admin_credentials="${var.proxysql_admin_user}:${var.proxysql_admin_password};${var.proxysql_readonly_user}:${var.proxysql_readonly_password}"
    mysql_ifaces="0.0.0.0:6032"
}

mysql_variables =
{
    threads=4
    max_connections=2048
    interfaces="0.0.0.0:6033"
    monitor_username="monitor"
    monitor_password="${var.password_1}"
}

# 마스터(10) / 슬레이브(20) 그룹 등록
mysql_servers =
(
    { address="${aws_instance.mysql_master.private_ip}", port=3306, hostgroup_id=10 },
    { address="${aws_instance.mysql_slave.private_ip}",  port=3306, hostgroup_id=20 }
)

# 복제 그룹 설정
mysql_replication_hostgroups =
(
    { writer_hostgroup=10, reader_hostgroup=20, comment="master-slave setup" }
)

# 애플리케이션 유저 → 기본은 master로 붙도록
mysql_users =
(
    { username = "${var.db_user}" , password = "${var.password_1}" , default_hostgroup = 10 , transaction_persistent = 0 }
)

# 쿼리 라우팅 규칙 (대소문자 무시, 공백 허용)
mysql_query_rules =
(
    { rule_id=1 , active=1 , match_pattern="(?i)^\\s*select\\b.*for\\s+update" , destination_hostgroup=10 , apply=1 },
    { rule_id=2 , active=1 , match_pattern="(?i)^\\s*select\\b"               , destination_hostgroup=20 , apply=1 }
)
EOC

# Master/Slave 준비될 때까지 대기 (컨테이너 기반 mysqladmin 사용)
until docker run --rm mysql:8.0 \
  mysqladmin ping -h ${aws_instance.mysql_master.private_ip} -uroot -p${var.password_1} --silent; do
  echo "waiting mysql-master..."
  sleep 5
done

until docker run --rm mysql:8.0 \
  mysqladmin ping -h ${aws_instance.mysql_slave.private_ip} -uroot -p${var.password_1} --silent; do
  echo "waiting mysql-slave..."
  sleep 5
done

# LitHub-prod 데이터베이스 생성 및 appuser에 DDL 권한 부여 (마스터에서 실행 → 슬레이브로 복제됨)
docker run --rm mysql:8.0 \
  mysql -h ${aws_instance.mysql_master.private_ip} -uroot -p${var.password_1} \
  -e "CREATE DATABASE IF NOT EXISTS \`LitHub-prod\`; \
      GRANT ALL PRIVILEGES ON \`LitHub-prod\`.* TO '${var.db_user}'@'%'; \
      FLUSH PRIVILEGES;"

# ProxySQL 컨테이너 실행
docker run -d \
  --name proxysql \
  --restart unless-stopped \
  --network host \
  -p 6032:6032 -p 6033:6033 \
  -v /db/proxysql/data:/var/lib/proxysql \
  -v /db/proxysql/conf/proxysql.cnf:/etc/proxysql.cnf \
  proxysql/proxysql

EOF

  # ---------------- Monitoring User Data ----------------
  monitoring_user_data = <<-EOF
#!/bin/bash
${local.common_user_data}

# 디렉토리 생성
mkdir -p /monitoring/prometheus /monitoring/grafana /monitoring/loki/{wal,index,cache,chunks}
mkdir -p /monitoring/grafana/provisioning/datasources
mkdir -p /monitoring/grafana/provisioning/dashboards
mkdir -p /monitoring/grafana/dashboards

# DNS 준비될 때까지 대기
for host in spring-blue.internal.local spring-green.internal.local; do
  until nslookup $host; do
    echo "Waiting for $host DNS record..."
    sleep 5
  done
done

# ---------------- Prometheus config ----------------
cat << 'EOC' > /monitoring/prometheus/prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: "spring-app"
    metrics_path: "/actuator/prometheus"
    static_configs:
      - targets: ["spring-blue.internal.local:8080", "spring-green.internal.local:8080"]
EOC

# ---------------- Loki config ----------------
cat << 'EOC' > /monitoring/loki/config.yml
auth_enabled: false

server:
  http_listen_port: 3100

ingester:
  lifecycler:
    ring:
      kvstore:
        store: inmemory
      replication_factor: 1
  chunk_idle_period: 5m
  chunk_retain_period: 30s
  wal:
    enabled: true
    dir: /loki/wal

schema_config:
  configs:
    - from: 2024-01-01
      store: boltdb-shipper
      object_store: filesystem
      schema: v12
      index:
        prefix: index_
        period: 24h

storage_config:
  boltdb_shipper:
    active_index_directory: /loki/index
    cache_location: /loki/cache
    shared_store: filesystem
  filesystem:
    directory: /loki/chunks

limits_config:
  retention_period: 168h   # 7일 보관
  reject_old_samples: true
  reject_old_samples_max_age: 168h
EOC

# ---------------- Grafana datasource provisioning ----------------
cat << 'EOC' > /monitoring/grafana/provisioning/datasources/datasource.yml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://localhost:9090
    isDefault: true

  - name: Loki
    type: loki
    access: proxy
    url: http://localhost:3100
EOC

# ---------------- Grafana dashboard provisioning ----------------
cat << 'EOC' > /monitoring/grafana/provisioning/dashboards/dashboard.yml
apiVersion: 1
providers:
  - name: 'default'
    orgId: 1
    folder: ''
    type: file
    disableDeletion: false
    editable: true
    options:
      path: /etc/grafana/dashboards
EOC

# ---------------- Grafana 기본 Dashboard ----------------
cat << 'EOC' > /monitoring/grafana/dashboards/unified.json
{
  "title": "Unified Monitoring (Metrics + Logs)",
  "templating": {
    "list": [
      {
        "name": "instance",
        "type": "query",
        "datasource": "Prometheus",
        "refresh": 1,
        "query": "label_values(http_server_requests_seconds_count, instance)"
      },
      {
        "name": "role",
        "type": "query",
        "datasource": "Loki",
        "refresh": 1,
        "query": "label_values({role!=\"\"}, role)"
      }
    ]
  },
  "panels": [
    {
      "type": "graph",
      "title": "Spring HTTP Requests per Second",
      "targets": [
        {
          "expr": "rate(http_server_requests_seconds_count{job=\\"spring-app\\",instance=~\\"$instance\\"}[1m])",
          "legendFormat": "{{instance}}"
        }
      ]
    },
    {
      "type": "graph",
      "title": "Spring JVM Memory (Heap)",
      "targets": [
        {
          "expr": "jvm_memory_used_bytes{job=\\"spring-app\\",instance=~\\"$instance\\",area=\\"heap\\"}",
          "legendFormat": "{{instance}}"
        }
      ]
    },
    {
      "type": "logs",
      "title": "Service Logs (filter by Role)",
      "targets": [
        {
          "expr": "{role=~\\"$role\\"}"
        }
      ]
    }
  ]
}
EOC

# ---------------- Prometheus 실행 ----------------
docker run -d \
  --name prometheus \
  --restart unless-stopped \
  --network host \
  -v /monitoring/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus

# ---------------- Loki 실행 ----------------
docker run -d \
  --name loki \
  --restart unless-stopped \
  --network host \
  -v /monitoring/loki:/loki \
  -v /monitoring/loki/config.yml:/etc/loki/config.yml \
  grafana/loki:2.9.3 -config.file=/etc/loki/config.yml

# ---------------- Grafana 실행 ----------------
docker run -d \
  --name grafana \
  --restart unless-stopped \
  --network host \
  -v /monitoring/grafana:/var/lib/grafana \
  -v /monitoring/grafana/provisioning:/etc/grafana/provisioning \
  -v /monitoring/grafana/dashboards:/etc/grafana/dashboards \
  grafana/grafana
EOF
}




  # ---------------- AMI ----------------
data "aws_ami" "latest_amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }
}





  # ---------------- USER EC2 ----------------
  resource "aws_instance" "haproxy" {
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_public.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    associate_public_ip_address = true
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    tags = { Name = "${var.prefix}-haproxy" }

    root_block_device {
      volume_type = "gp3"
      volume_size = 12
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.haproxy_user_data
  }

  resource "aws_instance" "app_blue" {
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_blue.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
    tags = {
      Name = "${var.prefix}-app-blue"
      Role = "active"
    }

    root_block_device {
      volume_type = "gp3"
      volume_size = 12
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.app_user_data
  }

  resource "aws_instance" "app_green" {
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_green.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
    tags = {
      Name = "${var.prefix}-app-green"
      Role = "standby"
    }

    root_block_device {
      volume_type = "gp3"
      volume_size = 12
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.app_user_data
  }

# ---------------- Redis Sentinel EC2 ----------------
  resource "aws_instance" "redis_master" {
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_redis_sentinel.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
    tags = { Name = "${var.prefix}-redis-master" }

    root_block_device {
      volume_type = "gp3"
      volume_size = 8
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.redis_master_user_data
  }

  resource "aws_instance" "redis_slave1" {
    depends_on                  = [aws_instance.redis_master]
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_redis_sentinel.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
    tags = { Name = "${var.prefix}-redis-slave1" }

    root_block_device {
      volume_type = "gp3"
      volume_size = 8
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.redis_slave_user_data
  }

  resource "aws_instance" "redis_slave2" {
    depends_on                  = [aws_instance.redis_master]
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_redis_sentinel.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
    tags = { Name = "${var.prefix}-redis-slave2" }

    root_block_device {
      volume_type = "gp3"
      volume_size = 8
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.redis_slave_user_data
  }

# ---------------- Redis Cache & RabbitMQ ----------------
  resource "aws_instance" "redis_cache" {
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_redis_mq.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
    tags = { Name = "${var.prefix}-redis-cache" }

    root_block_device {
      volume_type = "gp3"
      volume_size = 8
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.redis_cache_user_data
  }

  resource "aws_instance" "rabbitmq" {
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_redis_mq.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
    tags = { Name = "${var.prefix}-rabbitmq" }

    root_block_device {
      volume_type = "gp3"
      volume_size = 8
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.rabbitmq_user_data
  }

# ---------------- MySQL EC2 ----------------
  resource "aws_instance" "mysql_master" {
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_db.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
    tags = { Name = "${var.prefix}-mysql-master" }

    root_block_device {
      volume_type = "gp3"
      volume_size = 20
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.mysql_master_user_data
  }

  resource "aws_instance" "mysql_slave" {
    depends_on                  = [aws_instance.mysql_master]
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_db.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
    tags = { Name = "${var.prefix}-mysql-slave" }

    root_block_device {
      volume_type = "gp3"
      volume_size = 20
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.mysql_slave1_user_data
  }

  resource "aws_instance" "proxysql" {
    depends_on                  = [aws_instance.mysql_master, aws_instance.mysql_slave]
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_db.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
    tags = { Name = "${var.prefix}-proxysql" }

    root_block_device {
      volume_type = "gp3"
      volume_size = 10
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.proxysql_user_data
  }

# ---------------- Monitoring ----------------
  resource "aws_instance" "monitoring" {
    ami                         = data.aws_ami.latest_amazon_linux.id
    instance_type               = "t3.micro"
    subnet_id                   = aws_subnet.subnet_monitoring.id
    vpc_security_group_ids      = [aws_security_group.sg_1.id]
    iam_instance_profile        = aws_iam_instance_profile.instance_profile.name
    associate_public_ip_address = false
  tags = { Name = "${var.prefix}-monitoring" }

    root_block_device {
      volume_type = "gp3"
      volume_size = 15
    }

    metadata_options {
      http_tokens            = "required"
      instance_metadata_tags = "enabled"
    }

    user_data = local.monitoring_user_data
  }