variable "db_user" {
  description = "애플리케이션 DB 사용자명"
  type        = string
  default     = "appuser"
}

variable "password_1" {
  description = "password_1"
  default     = "1@2@3@4@"
}

variable "github_access_token_1" {
  description = "github_access_token_1, read:packages only"
  default     = "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"
} // 도커 재 로그인 시, 필요한 깃허브 토큰

variable "github_access_token_1_owner" {
  description = "github_access_token_1_owner"
  default     = "Jinyoung0718"
}

variable "proxysql_admin_user" {
  description = "ProxySQL 관리용 사용자명"
  type        = string
  default     = "superadmin"
}

variable "proxysql_admin_password" {
  description = "ProxySQL 관리용 비밀번호"
  type        = string
  default     = "StrongP@ssw0rd!"
}

variable "proxysql_readonly_user" {
  description = "ProxySQL read-only 관리 계정명"
  type        = string
  default     = "radmin"
}

variable "proxysql_readonly_password" {
  description = "ProxySQL read-only 관리 계정 비밀번호"
  type        = string
  default     = "ReadOnlyP@ss!"
}