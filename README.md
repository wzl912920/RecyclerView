# RecyclerViewDemo
	旧版本maven仓库已经全部删除，更新下新版使用方式
###
	该库最大的优点在于简化了adapter使用相关的代码
	使用时不需要关注viewtype类型，内部回自行判断viewtype
	同时也有一个问题就是，由于引入了kotlin的布局使用方式，必须有一个constructor(view)的构造，后续
	 会拆分单独kotlin的viewholder和java的viewholder
 
### Dependency
```gradle
implementation 'com.lynn.library:recyclerview:0.2.0'
```

```Java
    //该库只有一个adapter，其他的可以自由发挥，adapter所有方法保留
    recycler_view.adapter = adapter
    
    //注册方式
    //第一种，1对1注册（1个layout对应一个viewholder）
    adapter.register(R.layout.layout_demo , Demo1::class.java)
    /**
        class Demo1(containerView : View) : BaseViewHolder<String>(containerView) {
            override fun bind(data : String) {
                demo_text.setTextColor(Color.BLUE)
                demo_text.text = data
            }
        }
    */
    
    
    //第二种，同第一种，只不过将layoutid通过@LayoutId注解的方式添加到viewholder上，代码更简洁一些
    adapter.register(Demo2::class.java)
    /**
        @LayoutId(R.layout.layout_demo)
        class Demo2(containerView : View) : BaseViewHolder<Boolean>(containerView) {
            override fun bind(data : Boolean) {
                demo_text.setTextColor(Color.BLACK)
                demo_text.text = data.toString()
            }
        }
    */



    //第三种多对多,一种布局对应多种viewholder或者一个viewholder对应多种布局，或者多种viewholder对应多种布局 
    adapter.multiRegister(object : MultiTyper<Int> {
        override fun getLayoutId(data : Int) : Int {
            return R.layout.layout_demo
        }

        override fun getViewHolder(data : Int) : Class<out BaseViewHolder<Int>> {
            if (data == 0) {
                return DemoMultiOne::class.java
            }
            return DemoMultiTwo::class.java
        }
    })
    /**
        class DemoMultiOne(containerView : View) : BaseViewHolder<Int>(containerView) {
            override fun bind(data : Int) {
                demo_text.setTextColor(Color.RED)
                demo_text.text = data.toString()
            }
        }

        class DemoMultiTwo(containerView : View) : BaseViewHolder<Int>(containerView) {
            override fun bind(data : Int) {
                demo_text.setTextColor(Color.CYAN)
                demo_text.text = data.toString()
            }

            override fun onRefreshData(datas : MutableList<Any>) {
                super.onRefreshData(datas)
            }
        }
    */
    
    

    //注册全局点击事件，支持传入多个viewid针对view添加点击事件
    adapter.registerGlobalClickEvent(object : ItemClickEvent {
        override fun onItemClick(view : View , position : Int) {
            when (view.id) {
                R.id.button -> log("😜=======button")
                else -> log("😆-------")
            }
        }
    } , R.id.button)
    //长按
    adapter.registerGlobalLongClickEvent()
    
    
    //如果你某些view有单独的点击事件，可以通过重写viewholder中overrideGlobalClickEvent方法，屏蔽全局的点击事件
    override fun overrideGlobalClickEvent() : Boolean {
        return true
    }
    
    //支持局部刷新，见DemoMultiTwo中的onRefreshData
    
    //支持diffutil
    //第一种传入自定义diffutil
    fun refresh(diffUtil : DiffUtil.Callback) {
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
    }
    //第二种使用默认实现
    fun refresh(data : MutableList<Any>) {
        refresh(DiffCallback(data , list))
    }
```
