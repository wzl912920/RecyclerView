# simplerecyclerview
## 一个简化版的recyclerview
使用时只需要将type和layout注册到adapter，同时实现adapter的bindertools接口，bindertools主要用于根据数据获取数据type以及根据type获取viewholder，
其他方法和adapter用法一致，点击事件以及数据绑定均在viewholder种实现

### Dependency
```gradle
compile 'com.lynn.library:simple-recyclerview:0.0.2'
```

### example
#### 1  将viewtype与layout类型一一关联
```Java
val adapter : BaseRecycledAdapter<Object> = recycle_view.adapter as BaseRecycledAdapter<Object>
adapter.register(TYPE_NORMAL , R.layout.layout_test_type_normal)
adapter.register(TYPE_A , R.layout.layout_test_type_a)
adapter.register(TYPE_TEXT , R.layout.layout_test_type_text)
adapter.register(TYPE_IMG , R.layout.layout_test_type_img)
```
#### 2 实现BinderTools接口
```Java
adapter.setBinder(this)
//根据viewType获取ViewHolder
override fun getHolder(view : View , type : Int) : BaseViewHolder<*> {
}
//根据数据类型获取viewType
override fun <T : Any?> getType(t : T) : Int {
}
```
#### 3 实现ViewHolder view点击事件可以自行通过itemView获取到具体的view并实现onClickListener
```Java
class HolderNormal(itemView : View) : BaseViewHolder<DataNormal>(itemView) {
    var tv : TextView? = null
    init {
        tv = itemView.findViewById(R.id.text_view) as TextView?
    }

    override fun bind(data : DataNormal?) {
        tv?.text = "111111111111111111111"
    }
}
```
