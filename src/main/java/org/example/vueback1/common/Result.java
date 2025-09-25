package org.example.vueback1.common;

import lombok.Data;

@Data
public class Result {
    private int code;//编码 200 or 400
    private String msg;//成功 or 失败
    private Long total;//总记录数
    private Object data;//数据

    public static Result fail(){
        return result(400,"失败",0L,null);
    }

    public static Result suc(){
        return result(200,"成功",0L,null);
    }

    public static Result suc(Object data){
        return result(200,"成功",0L,data);
    }
    public static Result suc(Long total,Object data){
        return result(200,"成功",total,data);
    }

    private static Result result(int code,String msg,Long total,Object data){
        Result result=new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setTotal(total);
        result.setData(data);
        return  result;
    }
}
