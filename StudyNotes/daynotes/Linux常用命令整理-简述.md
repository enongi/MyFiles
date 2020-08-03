#                  [     Linux常用命令整理-for面试-简述        ](https://www.cnblogs.com/SevenwindMa/p/6443966.html)             

**Linux****命令**

**基本命令**

ll           查看所有文件详细信息

l           查看所有文件详细信息包括隐藏文件

ls           列出所有文件

ls  -a         列出所有文件包括隐藏

**pwd**          显示当前工作目录

cd /home/chang/a    进入目录a

cd           返回注册时目录(家目录) == cd ~

cd..          返回上级目录 

cd -          返回上一次目录

**mkdir**               新建目录 =md

mkdir  -p /home/chang/a/b/c   若a、b不存在照样创建

​    -m 777 /home/chang/a   建所有用户可读可写的a

**rmdir**  -p a/b/c         若a、b为空目录则递归删除目录

**touch** file     新建文件 

**cp**         将给出的文件或者目录**复制**到指定位置

cp file1 file1.bak  将file1复制到file1.bak

cp file1 file2 a1  将file1、file2拷贝到a1目录下

cp -r a1 a2     将a1目录拷贝到a2目录下  a2/a1

**mv**           为文件、目录**改名**或者将文件由一个目录**移动**到另一个目录

mv file1 file2     同一目录下file1重命名为file2

mv file1 file2 a   将file1和file2移至目录a

**rm** -rf file      强制删除文件file

**chown**  改变文件的属主、属组  chown -R epg:epg file

**chgrp**  改变文件或目录所属的组

**chmod**  改变文件的存取模式(r=4 w=2 x=1)

chmod  -R  777 file   递归赋权限，子目录也是777

chmod [ugoa][+-=][rwx] filename

chmod +w a.txt       增加写权限

chmod ug=rwx,o=x file 和 chmod 771 file 效果相同

chmod a=rwx file 和 chmod 777 file 效果相同

find -name filename   查找名为filename的目录/文件名

find ./ -name “*.c”

**ps**    查看进程

ps -e 显示出现在正在运行的所有进程

  -f 显示所有信息

  -u 显示指定用户进程

ps -ef|grep jboss/java/oracle   查看jboss/java/oracle进程

ps  -u epg            查看epg用户进程

kill -9 进程号           强制杀掉进程

passwd               改密码

shutdown -h now          关机

shutdown -r == reboot       重启

at 12:00 shutdown -s        12:00关机

tar  -cvf name.tar name      压缩文件/目录name，空目录拒绝压缩

tar -xvf name.tar         解压name.tar 

tar -czvf name.tar.gz name     压缩

tar -xzvf name.tar.gz à name    解压

zip:unzip  gz:gunzip bz:bunzip rar:unrar x  jar:java -jar

lsof -i:8082    查8082端口是否被占用

df -h        显示当前磁盘空间信息

df -Th        显示当前磁盘空间及类型

free         查看内存信息

top          查看CPU、内存利用率

ifconfig       查看本机ip地址及网卡信息

**grep** -i 字符串 *  查看当前目录下的所有文件中包含某字符串的行 -i：不区分大小写

**date**                 查看设置本机日期时间

date -R               显示时间加时区

date  -s  "2010-6-28 17:35:30"      修改日期时间

hwclock -w             将当前时间和日期写入BIOS，避免重启后失效

tail -f epglog.txt     实时查看运行日志

tailf epglog.txt > a.log  输出文件到另一个文件，文件重定向

tailf epglog.txt | tee a.log

**export** JAVA_HOME=hello!   设置环境变量JAVA_HOME

**echo $**JAVA_HOME       查看环境变量JAVA_HOME

**env**             显示所有的环境变量

<  输入重定向  

\>  输出重定向 ls > 1.txt,将文件列表的内容保存在1.txt文件中

\>>  追加重定向 将一条命令的输出结果追加到指定文件的末尾

|  管道    连接两个命令，第一个命令的输出作为第二个命令的输入

Wc<1.dat>2.result:从1.dat中读取数据给wc统计使用，统计结果放在2.result文件中

ls|grep users:将文件列表输出行中包含users的行显示出来．

 

**cat file   查看file文件内容，不能分页显示，适应于不超过一页的文件查看**

more     分页查看文件内容

less     分行查看文件内容

head -n file   显示文件的头n行

who      查看所有在线用户

who am i   查看当前用户详细信息

whoami    查看当前用户用户名

su - root  切换用户， 加‘-’ 表示用该用户的注册环境成为该用户

time   后面加命令，显示执行命令所用时间。例如：time ps –u chang

cal 【month】【year】   显示指定月份或者年的日历

history    所执行过的命令的历史记录

du -a 显示文件大小及目录总空间，其后可根文件名作参数

-s 仅显示指定目录所占空间的总和

uname -a   查linux版本号

logname    显示当前用户注册名

sourse filename  使修改的文件立即生效

hostname   显示主机名

clear  清屏

telnet IP地址   远程连接其他服务器

ftp IP地址

ping 检查两台服务器之间是否连通

java -version  查看JDK版本号

netstat -in/an  查看端口状态、IP地址

netstat -anp|grep 8082

netstat -in   查看网卡的IP地址

rpm -qa|grep 包名 检查系统包是否安装

rpm -ivh 包名    安装系统包

mstsc 远程桌面连接

tcpdump -i any host 机顶盒ip -s0 -w stb.cap 抓stb和服务器的包

 

 

 

**Vi****命令（编辑文件内容）**

​               

**1.****进入输入状态的命令**

a 从光标所在位置后面开始追加文字；

A 从光标所在行最后面的地方开始追加文字；

i 从光标所在位置前面开始插入文字；

I从光标所在行的行首开始插入文字；

o在光标所在行下新增一行；

O在光标所在行上方新增一行；

**2.****删除命令**

x删除光标所在字符；

dw删除光标所在整个单词；

dd删除光标所在的行；

**3.****修改命令**

r 修改光标所在字符，只修改单个字符；

R 修改光标所在行的多个字符；

**4.****内容的查找**

/content   向下查找content；

？content  向上查找content；

**5.****内容的替换**

:%s/old_word/new_word/g  用于在整个文件中替换特定字符串；

:s/old_word/new_word/g   用于在单行中替换特定字符串；

**6.****退出保存**（按esc键后）

:q 退出  :q! 强制退出  :w 保存  :wq = :x  保存退出

 

**7. 用户、群组管理**

1. 文件/etc/shadow ——存储帐户中用户加密后的口令及与口令有关的信息      

 　　  username:password:lastchg:min:max:warn:inactive:expire

1. 文件/etc/group——存储与用户帐户相关的组信息  格式：

　　　　grouname : password : gid : user-list  

1. 新增组

　　　　groupadd -g 2001 groupname

1. 删除组

　　　　groupdel  groupname

1. 新增用户

　　　　useradd  -g  groupname  -u 2010  -m -d /home/manager -s /bin/bash  username

1. 删除用户

　　　　userdel -rf username

1. 修改用户密码

　　　　passwd  username