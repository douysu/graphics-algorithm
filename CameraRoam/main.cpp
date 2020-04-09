extern "C"{
	int xPre;
    int yPre;
	float xDis;
	float yDis;
	bool isClick;//是否按下标志位
	float myabs(float a)
	{
		return  a>0 ? a : (-a);
	}
	static int32_t engine_handle_input(struct android_app* app, AInputEvent* event)
	{
		if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_MOTION)
		{
			if(AInputEvent_getSource(event)==AINPUT_SOURCE_TOUCHSCREEN)
			{
				int x=AMotionEvent_getRawX(event,0);
				int y=AMotionEvent_getRawY(event,0);
				int32_t id = AMotionEvent_getAction(event);
				switch(id)
				{
					case AMOTION_EVENT_ACTION_DOWN://按下事件
						xPre=x;
						yPre=y;
						isClick = true;
					break;
					case AMOTION_EVENT_ACTION_MOVE://滑动事件
						xDis=x-xPre;
						yDis=y-yPre;
						if (myabs(xDis)>5 || myabs(yDis)>5){//判断触控点位移是否超过阈值
							isClick = false;
						}
						if (!isClick){
							CameraUtil::calCamera(yDis*180.0f / 600, xDis*180.0f / 600);
						}
						xPre=x;
						yPre=y;
					break;
					case AMOTION_EVENT_ACTION_UP://抬起事件
						#define MOVE_SPAN 10
						if (isClick){
							if (x < MyVulkanManager::screenWidth / 4){//左移
								CameraUtil::cameraGo(0, MOVE_SPAN);
							}
							else if (x > MyVulkanManager::screenWidth * 3 / 4){//右移
								CameraUtil::cameraGo(0, -MOVE_SPAN);
							}
							else if (y < MyVulkanManager::screenHeight / 2){//前移
								CameraUtil::cameraGo(MOVE_SPAN, 0);
							}
							else{
								CameraUtil::cameraGo(-MOVE_SPAN, 0);//后移
							}
						}
					break;
				}
			}
			return true;
		}
		return false;
	}
}
