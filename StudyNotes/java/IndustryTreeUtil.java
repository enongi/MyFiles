package com.inspur.crds.platform.industryInfo;

import com.inspur.crds.platform.company.entity.IndustryDic;
import com.inspur.crds.platform.industryInfo.entity.IndustryInfoTree;
import com.inspur.crds.platform.industryInfo.entity.IndustryTreeNode;
import com.inspur.crds.platform.industryInfo.entity.SysIndustrytype;
import com.inspur.crds.platform.industryInfo.entity.SysInterIndusInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: IndustryTreeUtil
 * @Author: lizz
 * @Date: 2019/7/19.
 */
public class IndustryTreeUtil {
    /**
     * 两层循环实现建树
     *
     * @param treeNodes 传入的树节点列表
     * @return
     */
    public static <T extends IndustryTreeNode> List<T> build(List<T> treeNodes, Object root) {

        List<T> trees = new ArrayList<>();

        for (T treeNode : treeNodes) {

            if (root.equals(treeNode.getParentId())) {
                trees.add(treeNode);
            }

            for (T it : treeNodes) {
                if (it.getParentId().equals(treeNode.getCode())) {
                    if (treeNode.getChildren() == null) {
                        treeNode.setChildren(new ArrayList<>());
                    }
                    treeNode.add(it);
                }
            }
        }
        return trees;
    }

    /**
     * 使用递归方法建树
     *
     * @param treeNodes
     * @return
     */
    public static <T extends IndustryTreeNode> List<T> buildByRecursive(List<T> treeNodes, Object root) {
        List<T> trees = new ArrayList<>();
        for (T treeNode : treeNodes) {
            if (root.equals(treeNode.getParentId())) {
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    public static <T extends IndustryTreeNode> T findChildren(T treeNode, List<T> treeNodes) {
        for (T it : treeNodes) {
            if (treeNode.getCode().equals(it.getParentId())) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<>());
                }
                treeNode.add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }

    /**
     * SysInterIndusInfo
     *
     * @param indusInfos
     * @param root
     * @return
     */
    public static List<IndustryInfoTree> buildTree(List<SysInterIndusInfo> indusInfos, String root) {
        List<IndustryInfoTree> trees = new ArrayList<>();
        IndustryInfoTree node;
        for (SysInterIndusInfo indusInfo : indusInfos) {
            node = new IndustryInfoTree();
            node.setId(indusInfo.getId());
            node.setCode(indusInfo.getIndusCode());
            node.setParentId(indusInfo.getHigherCode());
            node.setIndusName(indusInfo.getIndusName());
            node.setIndusLevel(indusInfo.getIndusLevel());
            node.setIndusCode(indusInfo.getIndusCode());
            node.setHigherCode(indusInfo.getHigherCode());
            trees.add(node);
        }
        return IndustryTreeUtil.buildByRecursive(trees, root);
    }

    /**
     * SysInterIndusInfo
     *
     * @param indusInfos
     * @param root
     * @return
     */
    public static List<IndustryInfoTree> buildTree1(List<SysIndustrytype> indusInfos, String root) {
        List<IndustryInfoTree> trees = new ArrayList<>();
        IndustryInfoTree node;
        for (SysIndustrytype indusInfo : indusInfos) {
            node = new IndustryInfoTree();
            node.setId(indusInfo.getId());
            node.setCode(indusInfo.getIndustrynum().toString());
            node.setParentId(indusInfo.getHigherCode());
            node.setIndusName(indusInfo.getIndustryname());
            node.setIndusLevel(indusInfo.getClassification().toString());
            node.setIndusCode(indusInfo.getIndustrynum().toString());
            node.setHigherCode(indusInfo.getHigherCode());
            trees.add(node);
        }
        return IndustryTreeUtil.buildByRecursive(trees, root);
    }


    /**
     * 使用递归方法建树
     *
     * @param treeNodes
     * @return
     */
    public static List<IndustryDic> buildByRecursiveDic(List<IndustryDic> treeNodes, Object root) {
        List<IndustryDic> trees = new ArrayList<>();
        for (IndustryDic treeNode : treeNodes) {
            if (root.equals(treeNode.getRemarks())) {
                trees.add(findChildrenDic(treeNode, treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    public static IndustryDic findChildrenDic(IndustryDic treeNode, List<IndustryDic> treeNodes) {
        for (IndustryDic it : treeNodes) {
            if (treeNode.getValue().equals(it.getRemarks())) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<>());
                }
                treeNode.add(findChildrenDic(it, treeNodes));
            }
        }
        return treeNode;
    }
}
