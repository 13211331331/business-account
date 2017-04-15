package cn.billionsfinance.businessaccount.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 AsynWorker.doAsynWork(new Object[]{userId, ScoreExperienceEnum.REDISCUSS}, userExtendBiz,"increaseScoreExperience");
 */

/**
 * @author HuangHL
 * @date 2015/12/15
 * @see
 */
public class AsynWorker
{


    
    public static void doAsynWork(Object[] beans, Object serviceBean, String method)
    {
        new AsynWorker().new Request(serviceBean, method, beans).start();
    }

    private class Request
    {

        private String method;
        private Object service;
        private Object[] args;

        public Request(Object service, String method, Object[] args)
        {
            this.method = method;
            this.service = service;
            this.args = args;
        }

        protected void start()
        {  //开始请求
            ThreadPool.excute(new Runnable() {
                                  public void run() {
                                      try {
                                          Class<?>[] classes = new Class[args.length];
                                          for (int i = 0; i < args.length; i++) {
                                              if (null != args[i]) {
                                                  if (args[i].getClass().getName().indexOf("$") != -1) {
                                                      classes[i] = args[i].getClass().getSuperclass();
                                                  } else {
                                                      classes[i] = args[i].getClass();
                                                  }
                                                  if("OracleResultSetImpl".equals(classes[i].getSimpleName())){
                                                      classes[i] = java.sql.ResultSet.class;
                                                  }
                                              } else {
                                                  classes[i] = null;
                                              }
                                          }
                                          Method scoreMethod = null;
                                          try {
                                              scoreMethod = service.getClass().getDeclaredMethod(method, classes);
                                          } catch (NoSuchMethodException ex) {
                                              ex.printStackTrace();
                                          }
                                          if (scoreMethod != null) {
                                              try {
                                                  scoreMethod.invoke(service, args);
                                              } catch (IllegalAccessException e1) {
                                                  e1.printStackTrace();
                                              } catch (InvocationTargetException e1) {
                                                  e1.printStackTrace();
                                              }
                                          }
                                      } catch (Exception e) {
                                          e.printStackTrace();
                                      }

                                  }
                              }
            );
        }

    }

}
