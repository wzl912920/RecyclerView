# simplerecyclerview
## 由于第一个版本使用起来有点复杂，更新了一下使用方式

### Dependency
```gradle
compile 'com.lynn.library:simple-recyclerview:0.0.7'
```

同时新增了两个模块
```gradle
compile 'com.lynn.library:permission:0.0.7'//主要用于临时权限申请
compile 'com.lynn.library:util-kt:0.0.7'//工具类，该工具类为kotlin代码，非kotlin代码无法使用
```

### example
#### 1、  不再需要关注viewType，使用时仅需将layout和viewholder的class类型注册进adapter即可，对于同一种数据类型有不同布局时，需要实现MultiTyper接口
如下所示
```Java
//一种数据类型对应一种布局／viewholder
adapter.register(layoutId , DataModule::class.java)
//一种数据类型对应多种布局／viewholder
adapter.multiRegister(object : MultiTyper<DataModule> {
            override fun getLayoutId(data : DataModule) : Int {
                return layoutId
            }

            override fun getViewHolder(data : DataModule) : Class<out BaseViewHolder<DataModule>> {
                return ImgViewHolder::class.java
            }
        })
```

#### 2、由于省略了type类型，只需要继承BaseViewHolder实现自己的ViewHolder就可以了,当然点击事件什么的都是和viewholder绑定的，自己设置就好了，
下一个版本会考虑统一点击事件的设置
```Java
      class HolderNormal(act:Activity,itemView : View) : BaseViewHolder<DataNormal>(itemView) {
            private var tv : TextView
            private val sr = SoftReference(act)
            init {
                val lp = itemView.layoutParams
                lp.height = (itemView.context.screenHeight - itemView.context.statusBarHeight) / 3
                tv = itemView.findViewById<TextView>(R.id.text_view)
                itemView.setOnClickListener{ sr?.get()?.finish() }
            }

            override fun bind(data : DataNormal) {
                tv.text = "111111111111111111111"
            }
        }
```
