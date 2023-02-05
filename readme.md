# A Lengendary Project

 ## git bash 操作

 配置ssh秘钥
 ```
 cd   //先转至家目录
 ssh-keygen
 ls .ssh/
 cat id_rsa.pub  //查看，复制到ssh秘钥
 ```

 创建项目：

 ```
 git init       //初始化仓库
 vim readme.md  //或 创建readme.md文件
 git status     //查看
 git add .      //将所有文件上传至仓库
 git commit -m "创建项目" //提交
 git remote add origin 仓库网址   
 git push -u origin master
 ```

克隆仓库
 ```
 通过ssh克隆(fork)，将链接复制下来
 在目标文件夹下：
 git clone 链接
 ```

同步仓库
上传（在目标文件下）
```
git status
git add .
git commit -m "test"
git push
```

下载（在目标文件下）
```
git pull
```



## 前端相关（vue）

### web端

插件: router, vuex

依赖: jquery, boostrap



#### 导航栏

两种实现方式：

第一种：

```vue
<template>
 ......
 <li class="nav-item">
   <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'pk_index'}">对战</router-link>
 </li>
 ......
</template>

<script>
import { useRoute } from 'vue-router'
import { computed } from 'vue'

export default {
    setup() {
        const route = useRoute();
        let route_name = computed(() => route.name)
        return {
            route_name
        }
    }
}
</script>

<style scoped>
</style>
```

第二种：
```vue
<template>
 ......
 <li class="nav-item">
   <router-link class="nav-link" active-class="active" aria-current="page" :to="{name: 'pk_index'}">匹配对战</router-link>
 </li>
 ......
</template>
......
```
#### 经验杂谈

```
关于js的import：
当export default时，import的类不需要加括号{}；
否则要加括号{}
```




## 后端相关


### mySQL数据库 (8.0版本)

mysql服务关闭： `net stop mysql80`

mysql服务启动: `net start mysql80`

常用操作:
```sql
mysql -uroot -p
输入密码
//进入了mysql命令行
show databases; //显示多少个数据库
create database <dbname>; //创建数据库
drop database <dbname>;   //删除数据库
use <dbname>;  //使用某个数据库
show tables;  //使用某个数据库时，展示所有表
create table user(id int, username varchar(100), password varchar(100)); //创建表
drop table user; //删除表
insert into user values(1, 'hxc', '123123'); //插入数据
select * from user; //查询数据
select * from user where id=1; //查询符合条件的数据
delete from user where id=1; //删除符合条件的数据
```
