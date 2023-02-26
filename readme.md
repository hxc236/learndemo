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
在github修改过文件时，将远程仓库与本地仓库同步：
git pull --rebase origin master
```

代码量统计
```
find -name '*.java' | xargs cat | wc -l
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

## 学习开发过程中的疑问

1、WebSocket协议和http协议区别，工作原理？

2、Bean的含义？@Autowired 含义？
用法及个人理解：大概是需要用到啥就在这个类前加个@Configuration，然后用到什么方法就在该方法前面加一个@Bean，然后根据<类型>对应一个@Autowired，被@Autowired修饰的变量会直接用@Bean修饰的方法中类型匹配的注入到该变量中;
@Autowired写在set()方法上，在spring会根据方法的参数类型从ioc容器中找到该类型的Bean对象注入到方法的形参中，被@Autowired修饰的方法一定会执行, 所以一般使用在set方法中, 普通方法不用。

3、如何把URL写到配置文件里？（需要浅学一下cloud）

4、openfegin负载均衡？


## 后传

### 经验杂谈

前端小tip? (maybe，待证明

```
关于js的import：
当export default时，import的类不需要加括号{}；
否则要加括号{}
```

> 关于前端vue我把class写成calss找了1小时bug心态爆炸这件事

过程不想再经历了。。。真的是，小小的错误会导致怪怪的bug，当出现了一些奇怪的事情时，一般并不是关键代码出了问题，果然前进的过程是曲折的。。。

> 关于后端我找了1个小时bug结果是注解@RestController写成了@Controller这件事

太痛了。。。。。。。。。。。。。通过前端setup和postman一起测试post类型请求，403和500我都能接受，怎么会404呢？？没想到啊，我是傻比我是傻比...

**所以说啊同志们，一定要记清楚注解啊！！**



## 出大问题

不会安装docker，卡在项目上线了。。。得先学学Linux了...
