## java static final 初始化

1.static修饰（类变量）一个属性字段,那么这个属性字段将成为类本身的资源,public修饰为共有的,可以在类的外部通过test.a来访问此属性;在类内部任何地方可以使用.如果被修饰为private私有,那么只能在类内部使用. 



1. public class Test{
2. public static int a;//类连接时，默认初始为0，而又无类变量初始化语句或者静态初始化语句，故此类无类初始化方法<clinit>
3. private Test(){
4. a=0;//类实例化时候调用
5. }
6. }

如果属性被修饰为static静态类资源,那么这个字段永远只有一个,也就是说不管你new test()多少个类的对象,操作的永远都只是属于类的那一块内存资源.例如: 



1. Test t1=new Test();
2. t1.a=10;
3. Test t2=new Test();
4. System.out.println(t1.a);
5. System.out.println(t2.a);
6. System.out.println(Test.a);

*代码* 结果是3个10

2.final 用于声明属性（常量），方法和类，分别表示属性一旦被分配内存空间就必须初始化（不会有默认初始化，局部变量也是如此，默认初始化只有普通的非final属性，对于static（无final修饰）类变量，类连接时候有默认初始化，对于像private int a;在类实例化时，构造函数默认初始为0，总之，变量必须初始化后方可用，这是java的安全之一，例如，对象引用初始化后不会引用错误的内存空间）,并且以后不可变;方法一旦定义必须有实现代码并且子类里不可被覆盖，类一旦定义不能被定义为抽象类或是接口,因为不可被继承。

**
\3. 被final修饰而没有被static修饰的类的属性变量只能在两种情况下初始化:(必须初始化）**
对于接口，由于只能包含常量和方法（必须wei 
a.在它被声明的时候赋值,例: 



1. public class Test{
2. public final int a=0;
3. private Test(){
4. }
5. }

b.在构造函数里初始化

例如：

1. public class Test{
2. public final int a;
3. private Test(){
4. a=0;
5. }
6. }

c、在非静态块里

public class Test{

​     private final int a;

​    {

​       a=9;

​     }

}
**解释**：当这个属性被修饰为final,而非static的时候,它属于类的实例对象的资源(实例常量）,当类被加载进内存的时候这个属性并没有给其分配内存空间,而只是 定义了一个变量a,只有当类被实例化的时候这个属性才被分配内存空间,而实例化的时候同时执行了构造函数,所以属性被初始化了,也就符合了当它被分配内存 空间的时候就需要初始化,以后不再改变的条件.

**4、 被static修饰而没有被final修饰的类的属性变量只能在两种情况下初始化:(可以不初始化）**

如果初始化，就生成类初始化函数<clinit>,否则没有
a.在它被声明的时候赋值,例:

1. public class Test{
2. public static l int a=8;
3. private Test(){
4. }
5. }

b.在静态或非静态快里初始化

1. public class Test{
2. public static l int a;
3. static{a=50;}
4. private Test(){
5. }
6. }

**解释：**
当类的属性被同时被修饰为static时候，他属于类的资源（类变量）,在类加载后，进行连接时候，分三步： 先验证；然后准备，准备时，先分配内存，接着默认初始化；可以进行解析。最后，进行类初始化，类初始化前，必须保证它的父类已经初始化了，所以最先初始化的是超类，对于接口，不必初始其父接口。类初始化时，它把类变量初始化语句及静态初始化语句放到类初始化方法中，所以，如果无此两种语句，也就没<clinit>类初始化方法,而构造函数是在当类 被实例化的时候才会执行,所以用构造函数,这时候这个属性没有被初始化.程序就会报错.而static块是类被加载的时候执行,且只执行这一次,所以在 static块中可以被初始化.

**5.同时被final和static修饰的类的属性变量只能在两种情况下初始化:（必须初始化）**

a.在它被定义的时候,例: 



1. public class Test{
2. public final static int a=5;
3. private Test(){
4. }
5. }

b.在类的静态块里初始化,例: 



1. public class Test{
2. public final static int a;
3. static{
4. a=0;
5. }
6. }

*c、特别对于初始化*时候调用抛出异常的构造函数，初始时候注意，特别是在实现单例模式时(只能这么初始化）
如：
class A
{ 
private final static A a;
static
{
try
{
a=new A();
}catch(Exception e)
{
throws new RuntimeException(e);     //必须有，不然不能完成常量的正确初始化
}
}
private A() throws Exception{}
**}
解释：**
当类的属性被同时被修饰为static和final的时候，他属于类的资源（类常量）,那么就是类在被加载进内存的时候(也就是应用程 序启动的时候)就要已经为此属性分配了内存,所以此时属性已经存在,它又被final修饰,所以必须在属性定义了以后就给其初始化值.而构造函数是在当类 被实例化的时候才会执行,所以用构造函数,这时候这个属性没有被初始化.程序就会报错.而static块是类被加载的时候执行,且只执行这一次,所以在 static块中可以被初始化.