/**
 * 检查权限（判断当前登录用户是否）
 * @param loginUser 当前登录用户
 * @param needAccess 需要有的权限
 * @retuen boolean 有无权限
 */
import ACCESS_ENUM from "@/access/accessEnum";
import AccessEnum from "@/access/accessEnum";

const checkAccess=(loginUser:any,needAccess=ACCESS_ENUM.NOT_LOGIN)=>{
    //判断当前登录用户具有的权限
    const loginUserAccess = loginUser?.userRole??ACCESS_ENUM.NOT_LOGIN

    if(needAccess===ACCESS_ENUM.NOT_LOGIN){//即所需的权限是无
        return true;
    }
    //如果用户登录才能访问
    if(needAccess===AccessEnum.USER){//即所需的权限是
        //如果用户没登录，那么表示无权限
        if(loginUserAccess!==ACCESS_ENUM.NOT_LOGIN){
            return true;
        }
    }
    if(needAccess===ACCESS_ENUM.ADMIN){
        if(loginUserAccess!==ACCESS_ENUM.ADMIN){
            return false;
        }
    }
    return true;
}
export default checkAccess;