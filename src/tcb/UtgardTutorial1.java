package tcb;

import java.util.concurrent.Executors;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;

public class UtgardTutorial1 {
 
    public static void main(String[] args) throws Exception {
        // ������Ϣ
        final ConnectionInformation ci = new ConnectionInformation(); 
        ci.setHost("10.128.75.231");         // ����IP
        ci.setDomain("");                  // ��Ϊ�վ���
        ci.setUser("OPCServer");             // �������Լ����õ��û���
        ci.setPassword("root");          // �û���������

        // ʹ��MatrikonOPC Server������
        // ci.setClsid("F8582CF2-88FB-11D0-B850-00C0F0104305"); // MatrikonOPC��ע���ID�������ڡ���������￴��
        // final String itemId = "u.u";    // ������ְ�ʵ��

        // ʹ��KEPServer������
        ci.setClsid("7BC0CC8E-482C-47CA-ABDC-0FE7F9C6E729"); // KEPServer��ע���ID�������ڡ���������￴��
        final String itemId = "channel.device01.temperature_sensor";    // ������ְ�ʵ�ʣ�û��ʵ��PLC���õ�ģ������simulator
        // final String itemId = "ͨ�� 1.�豸 1.��� 1";
 
        // ��������
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
 
        try {
            // ���ӵ�����
            server.connect();
            // add sync access, poll every 500 ms������һ��ͬ����access������ȡ��ַ�ϵ�ֵ���̳߳�ÿ500ms��ֵһ��
            // ���������ѭ����ֵ�ģ�ֻ��һ��ֵ��������
            final AccessBase access = new SyncAccess(server, 500);
            // ���Ǹ��ص����������Ƕ���ֵ��ִ�������ӡ������������д�ģ���ȻҲ����д������ȥ
            access.addItem(itemId, new DataCallback() {
                @Override
                public void changed(Item item, ItemState itemState) {
                    int type = 0;
					try {
						type = itemState.getValue().getType(); // ����ʵ�������֣��ó��������
					} catch (JIException e) {
						e.printStackTrace();
					}
                    System.out.println("���������������ǣ�-----" + type);
                    System.out.println("������ʱ����ǣ�-----" + itemState.getTimestamp().getTime());
                    System.out.println("��������ϸ��Ϣ�ǣ�-----" + itemState);
 
                    // ���������short���͵�ֵ
                    if (type == JIVariant.VT_I2) {
                        short n = 0;
						try {
							n = itemState.getValue().getObjectAsShort();
						} catch (JIException e) {
							e.printStackTrace();
						}
                        System.out.println("-----short����ֵ�� " + n); 
                    }
 
                    // ����������ַ������͵�ֵ
                    if(type == JIVariant.VT_BSTR) {  // �ַ�����������8
                        JIString value = null;
						try {
							value = itemState.getValue().getObjectAsString();
						} catch (JIException e) {
							e.printStackTrace();
						} // ���ַ�����ȡ
                        String str = value.getString(); // �õ��ַ���
                        System.out.println("-----String����ֵ�� " + str); 
                    }
                }
            });
            // start reading����ʼ��ֵ
            access.bind();
            // wait a little bit���и�10����ʱ
//            Thread.sleep(10 * 1000);
            // stop reading��ֹͣ��ȡ
            access.unbind();
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }
}