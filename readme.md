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
