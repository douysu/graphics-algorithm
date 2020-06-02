package wyf.lxg.beike;

/**
 * Created by Administrator on 2017/7/20.
 */
public class BeiKeThread extends Thread
{
    AllBeiKe ak;
    public BeiKeThread(AllBeiKe ak)
    {
        this.ak=ak;
    }
    public boolean flag=true;
    float addOrsub=-1.0f;//1为加-1为减
    float angleStep=0.5f;//角度的步长
    @Override
    public void run()
    {
        while(flag)
        {
            if(ak.BeiKeAngle<=-38){
                addOrsub=-addOrsub;

            }else if(ak.BeiKeAngle>-5.0){
                addOrsub=-addOrsub;
            }
            ak.BeiKeAngle=ak.BeiKeAngle+addOrsub*angleStep;
            try {Thread.sleep(80);}
            catch(Exception e) {e.printStackTrace();}
        }
    }

}