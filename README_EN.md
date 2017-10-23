# simplerecyclerview
                        just translate the README to english with my broken english

### Dependency
```gradle
compile 'com.lynn.library:simple-recyclerview:0.1.1'
```

Add two new module
```gradle
compile 'com.lynn.library:permission:0.0.7'//Uses In Requesting Permissions
compile 'com.lynn.library:util-kt:0.0.8'//a kotlin util, only kotlin project is available
```

### example
#### 1、No need to pay attention on viewType,just register the layoutId and the class of the DataModule to adapter
如下所示
```Java
        //one DataMudle to one layout／viewholder
        adapter.register(layoutId , DataModule::class.java)
        //one DataModule to many layout／viewholder
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

#### 2、extends the BaseViewHolder,and ovveride the bind method to bind you data
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
#### 3、add itemClickEvent
```Java
//you should register one ItemClickEvent and null or many viewIds
adapter.registerGlobalClickEvent(object : ItemClickEvent {
            override fun onItemClick(view : View , potision : Int) {
                if (view.id == R.id.text_view) {
                    showWarning("✅✅✅✅")
                } else {
                    showError("xxxxxxxx")
                }
            }
        } , R.id.text_view)
//the longClickEvent just call registerGlobalLongClickEvent, and is the same usage as ItemClickEvent

//if you registed Gloable ClickEvent and you need a special deal way for some Holder,just make the overrideGlobalClickEvent method return true,and do your own clickEvent in the ViewHolder
override fun overrideGlobalClickEvent() : Boolean {
    return true
}
```
#### version 0.1.1 add local refresh
```Java
//the same usages as adapter,only the bind data part move to viewHolder
        recycle_view.adapter.notifyItemChanged(1 , Temp("xxxx.jpg"))
//override baseviewholder's bind method
        override fun bind(data : DataImg , payLoads : MutableList<Any>) {
                val t = payLoads[0] as Temp
                image_view.setImageURI(Uri.parse(t.img))
                text_view.text = t.s.s
        }
```









