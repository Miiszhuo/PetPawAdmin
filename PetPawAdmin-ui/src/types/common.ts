// 通用类型定义

export interface PageParams {
  pageNum: number;
  pageSize: number;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  current: number;
  size: number;
  pages: number;
}

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

export interface SelectOption {
  label: string;
  value: string | number;
  disabled?: boolean;
}

export interface TreeNode {
  id: number | string;
  label: string;
  children?: TreeNode[];
  disabled?: boolean;
  [key: string]: any;
}
