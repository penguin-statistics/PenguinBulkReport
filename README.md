# PenguinBulkReport

## 企鹅数据本地批量上传
Ver. demo-0.0.3  
Author. Strontium

## 问题
1. 目前没有增加任何的错误检测  
~~2. 掉落物数量全部编入body后依旧产生空白的无意义上传quest  
为了避免过早把list上传完，目前这些空白request是必须的~~  
2019-08-18 修复

## 重大bug（待修复，咕咕咕）
~~1/常规掉落和额外掉落存在相同种类时上传数据会产生违反limitation的上传~~  
2019-08-07已修复  
~~2/ 前端由多种类掉落切换至少种类掉落时，选框不消失  
3/ 家具数切换stage时不保存~~  
2019-08-19已修复

## Update History
### Ver 0.0.1
* First Working version  
### Ver 0.0.2
* Auto-save  
* Meta-stages data reversion  
### Ver 0.0.3
* 左键物品图标+1，右键-1

### 目前上传功能已经写好
但是实际没有实装  
只会在控制台输出上传数据+会弹出窗口展示request body(所以是demo版)  
目的是为了减少站长削除异常数据的工作量

### Side Notes
本质企鹅数据DDOS机（大雾

