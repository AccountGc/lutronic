package com.e3ps.common.util.service;

import java.util.ArrayList;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.introspection.WTIntrospectionException;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.e3ps.common.util.Tree;

/**		com.e3ps.common.util.TreeHelper
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class StandardTreeService extends StandardManager implements TreeService {

	public static StandardTreeService newStandardTreeService() throws Exception {
		final StandardTreeService instance = new StandardTreeService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public ArrayList getAllChildList(Class treeClass, Tree tree, ArrayList returnlist) {
        return getAllChildList(treeClass, tree, null, returnlist);
    }
	
	/**
     * _tree �� ��� ���� ��ü�� �����Ѵ�.
     * @param _treeClass
     * @param _tree
     * @param _sort _tree�� �ʵ�(�������� ����)
     * @param _returnlist new ArrayList() �� ������ ����
     * @return
     */
    public ArrayList getAllChildList(Class _treeClass, Tree _tree, String _sort, ArrayList _returnlist) {
        try {
            QuerySpec spec = getChildQuerySpec(_treeClass, _tree, _sort);
            QueryResult qr = PersistenceHelper.manager.find(spec);
            while (qr.hasMoreElements())
            {
                Object[] obj = (Object[]) qr.nextElement();
                _returnlist.add(obj[0]);
                getAllChildList(_treeClass, (Tree) obj[0], _sort, _returnlist);
            }
        }
        catch (QueryException e) {
            e.printStackTrace();
        }
        catch (WTException e) {
            e.printStackTrace();
        }
        catch (WTPropertyVetoException e) {
            e.printStackTrace();
        }
        return _returnlist;
    }
    
    /**
     * ���� ��ü�� �˻��� ���� QuerySpec �� ��ȯ
     * @param _treeClass
     * @param _tree
     * @param _sort _tree�� �ʵ�(�������� ����)
     * @return
     * @throws WTPropertyVetoException
     * @throws QueryException
     * @throws WTIntrospectionException
     */
    public QuerySpec getChildQuerySpec(Class _treeClass, Tree _tree, String _sort) throws WTPropertyVetoException,
            QueryException, WTIntrospectionException {
        QuerySpec spec = new QuerySpec();
        int idx = spec.addClassList(_treeClass, true);
        
        spec.appendWhere(new SearchCondition(_treeClass, "parentReference.key", "=", PersistenceHelper.getObjectIdentifier(_tree)), new int[] { idx });

        if(_sort == null) {
			ClassAttribute classattribute = new ClassAttribute(_treeClass, "thePersistInfo.createStamp");
			classattribute.setColumnAlias("createDate" + String.valueOf(0));
			int[] fieldNoArr = { idx };
			spec.appendSelect(classattribute, fieldNoArr, false);
			OrderBy orderby = new OrderBy(classattribute, true, null);
			spec.appendOrderBy(orderby, idx);
        }else
			spec.appendOrderBy(new OrderBy(new ClassAttribute(_treeClass, _sort), false), new int[] { idx });

		return spec;
    }
}
