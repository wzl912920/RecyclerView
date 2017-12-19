# simplerecyclerview

### Dependency
```gradle
compile 'com.lynn.library:simple-recyclerview:0.1.1'
```

同时新增了两个模块
```gradle
compile 'com.lynn.library:permission:0.0.7'//主要用于临时权限申请
compile 'com.lynn.library:util-kt:0.0.8'//工具类，该工具类为kotlin代码，非kotlin代码无法使用
```

### example
#### 1、  不再需要关注viewType，使用时仅需将layout和viewholder的class类型注册进adapter即可，对于同一种数据类型有不同布局时，需要实现MultiTyper接口
如下所示
```Java
        //一种数据类型对应一种布局／viewholder
        adapter.register(layoutId , DataModule::class.java)
        //一种数据类型对应多种布局／viewholder
        adapter.multiRegister(object : MultiTyper<DataNormal> {
            override fun getLayoutId(data : DataNormal) : Int {
                return R.layout.layout_test_type_normal
            }

            override fun getViewHolder(data : DataNormal) : Class<out BaseViewHolder<DataNormal>> {
                if (data.type == 1) {
                    return NormalHolderA::class.java
                }
                return NormalHolderB::class.java
            }

        })

```

#### 2、由于省略了type类型，只需要继承BaseViewHolder实现自己的ViewHolder就可以了,支持直接引用layout中布局id，但是请保证多个layout中不存在相同的id，否则会报错，同时请保证只有一个constructor(view)（：可以有多个constructor，但必须有单个view的constructor，因为这里是通过反射构造的viewholder，所以实际你其他的constructor并没有效果）
```Java
        class NormalHolderB(containerView : View) : BaseViewHolder<DataNormal>(containerView) {
            init {
                val lp = itemView.layoutParams
                lp.height = (itemView.context.screenHeight - itemView.context.statusBarHeight) / 3
            }
            override fun bind(data : DataNormal) {
                text_view.text = "BBBBBBBBBBB"
            }
        }
```
#### 3、添加全局点击事件
```Java
//可以传数个你需要绑定的viewId，无论有没有传viewId，该点击事件都会同时注册到item上
adapter.registerGlobalClickEvent(object : ItemClickEvent {
            override fun onItemClick(view : View , potision : Int) {
                if (view.id == R.id.text_view) {
                    showWarning("✅✅✅✅")
                } else {
                    showError("xxxxxxxx")
                }
            }
        } , R.id.text_view)
//长按事件调用registerGlobalLongClickEvent其他一致

//如果你有配置了全局的点击事件，同时还想要某个ViewHolder有自己的一套处理逻辑，可以在viewholder里单独设置点击事件，同时重写overrideGlobalClickEvent方法，返回true即可
override fun overrideGlobalClickEvent() : Boolean {
    return true
}
```
#### 0.1.1版本新增了局部刷新-使用方式同Adapter
```Java
//调用方式同adapter原生方式，实现移到viewholder中
        recycle_view.adapter.notifyItemChanged(1 , Temp("xxxx.jpg"))
//实现BaseViewHolder的如下方法
        override fun bind(data : DataImg , payLoads : MutableList<Any>) {
                val t = payLoads[0] as Temp
                image_view.setImageURI(Uri.parse(t.img))
                text_view.text = t.s.s
        }
```



