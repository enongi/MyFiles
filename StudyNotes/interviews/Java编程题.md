## Java编程题

### 字符串反转的四种方法

1,利用字符串的拼接(charAt()方法),把后遍历出来的放在前面即可实现反转

```java
public static String charAtReverse (String s){
   int length = s.length();
   String reverse = " ";
   for (int i = 0; i < length; i++) {
    reverse = s.charAt(i)+reverse;//字符串中获取单个字符的字符的放法
   }
   return reverse;
  }
```

2,利用字符串的拼接(toCharArray()处理成字符数组的方法),把后遍历出来的放在前面即可实现反转

```java
public static String reverseCharArrays(String s){
   char []array = s.toCharArray();//把字符串分割成单个字符的数组
   String reverse = "";
   for(int i = array.length -1 ; i>=0 ; i--){//遍历数组,从后向前拼接
    reverse +=array[i];
   }
   return reverse;
  }
```

3,利用StringBuffer的reverse()方法

```java
public static String reverseStringBuffer(String s){
   StringBuffer sb = new StringBuffer(s);
   String afterReverse = sb.reverse().toString();
   return afterReverse;
  }
```

4,利用递归的方法,类似与二分查找的折半思想

```java
public static String reverseRecursive(String s){
   int length = s.length();
   if(length<=1){
    return s;
   }
   String left  = s.substring(0,length/2);
   String right = s.substring(length/2 ,length);
   String afterReverse = reverseRecursive(right)+reverseRecursive(left);//此处是递归的方法调用
   return afterReverse;
  }
```

