package core.algorithm_tree;

import java.util.ArrayList;
import core.*;
import utils.TRandomGenerator;

public class TInstructionsTree {
  TInstructionNode fRootNode;
  ArrayList<TNode> fLeafNodes = new ArrayList<TNode>();
  ArrayList<TNode> fAllNodes = new ArrayList<TNode>();

  public TInstructionsTree(TMemoryType rootMemoryType, TComponentType componentType,
      TAllowedOpsManager opsManager, int numOfOps, ArrayList<TNode> leaves, TRandomGenerator rand) {
    if (numOfOps < 1) {
      throw new Error("numOfOps must be grater than 1");
    }

    // ルートノードの作成
    TOp rootOp = opsManager.getOutMemoryTypeDesignatedRandomOp(rootMemoryType, componentType, rand);
    fRootNode = new TInstructionNode(rootMemoryType, rootOp);
    ArrayList<TInstructionNode> currentAvailableNodes = new ArrayList<TInstructionNode>();
    currentAvailableNodes.add(fRootNode);
    fAllNodes.add(fRootNode);
    numOfOps--;

    // ルートよりも下の葉以外のノードを構築
    while (numOfOps > 0) {
      int availableIndex = rand.nextInt(currentAvailableNodes.size());
      TInstructionNode node = currentAvailableNodes.get(availableIndex);
      switch (node.getAvailableInput(rand)) {
        case 0: {
          TMemoryType childNodeMemoryType = node.getIn1MemoryType();
          TOp op = opsManager.getOutMemoryTypeDesignatedRandomOp(childNodeMemoryType, componentType,
              rand);
          TInstructionNode child = new TInstructionNode(childNodeMemoryType, op);
          node.setIn1Node(child);
          if (node.isFilled()) {
            currentAvailableNodes.remove(availableIndex);
          }
          currentAvailableNodes.add(child);
          fAllNodes.add(child);
          break;
        }

        case 1: {
          TMemoryType childNodeMemoryType = node.getIn2MemoryType();
          TOp op = opsManager.getOutMemoryTypeDesignatedRandomOp(childNodeMemoryType, componentType,
              rand);
          TInstructionNode child = new TInstructionNode(childNodeMemoryType, op);
          if (node.isFilled()) {
            currentAvailableNodes.remove(availableIndex);
          }
          currentAvailableNodes.add(child);
          fAllNodes.add(child);
          break;
        }

        default:
          throw new Error("Unexpected.");
      }
    }
    
    // 葉を構築
    ArrayList<TMemoryType> assignableMemoryTypes = new ArrayList<TMemoryType>();
    for (TInstructionNode availableNode : currentAvailableNodes) {
      if (availableNode.isAvailableIn1()) {
        assignableMemoryTypes.add(availableNode.getIn1MemoryType());
      } else {
        assignableMemoryTypes.add(null);
      }
      if (availableNode.isAvailableIn2()) {
        assignableMemoryTypes.add(availableNode.getIn2MemoryType());
      } else {
        assignableMemoryTypes.add(null);
      }
    }

    ArrayList<Integer> scalarAssignableIndices = new ArrayList<Integer>();
    ArrayList<Integer> vectorAssignableIndices = new ArrayList<Integer>();
    ArrayList<Integer> matrixAssignableIndices = new ArrayList<Integer>();
    for (int index = 0; index < assignableMemoryTypes.size(); index++) {
      TMemoryType assignableMemoryType = assignableMemoryTypes.get(index);
      switch (assignableMemoryType) {
        case SCALAR:
          scalarAssignableIndices.add(index);
          break;
        case VECTOR:
          vectorAssignableIndices.add(index);
          break;
        case MATRIX:
          matrixAssignableIndices.add(index);
          break;
      }
    }

    ArrayList<TNode> scalarLeafNodeCandidates = new ArrayList<TNode>();
    ArrayList<TNode> vectorLeafNodeCandidates = new ArrayList<TNode>();
    ArrayList<TNode> matrixLeafNodeCandidates = new ArrayList<TNode>();

    for (TNode leaf : leaves) {
      int index;
      switch (leaf.getMemoryType()) {
        case SCALAR:
          index = scalarAssignableIndices.remove(rand.nextInt(scalarAssignableIndices.size()));
          scalarLeafNodeCandidates.add(leaf);
          break;

        case VECTOR:
          index = vectorAssignableIndices.remove(rand.nextInt(vectorAssignableIndices.size()));
          vectorLeafNodeCandidates.add(leaf);
          break;

        case MATRIX:
          index = matrixAssignableIndices.remove(rand.nextInt(matrixAssignableIndices.size()));
          matrixLeafNodeCandidates.add(leaf);
          break;

        default:
          throw new Error("Unreachable");
      }
      int availableIndex = index / 2;
      switch (index % 2) {
        case 0:
          currentAvailableNodes.get(availableIndex).setIn1Node(leaf);
          break;
        case 1:
          currentAvailableNodes.get(availableIndex).setIn2Node(leaf);
          break;
        default:
          throw new Error("Unreachable");
      }
      assignableMemoryTypes.set(index, null);
    }

    for (int assignableMemoryTypeIndex = 0; assignableMemoryTypeIndex < assignableMemoryTypes.size(); assignableMemoryTypeIndex++) {
      TMemoryType assignableMemoryType = assignableMemoryTypes.get(assignableMemoryTypeIndex);
      if (assignableMemoryType == null)
        continue;
      TNode node;
      switch (assignableMemoryType) {
        case SCALAR:
          node = scalarLeafNodeCandidates.remove(rand.nextInt(scalarLeafNodeCandidates.size()));
          break;
        case VECTOR:
          node = vectorLeafNodeCandidates.remove(rand.nextInt(vectorLeafNodeCandidates.size()));
          break;
        case MATRIX:
          node = matrixLeafNodeCandidates.remove(rand.nextInt(matrixLeafNodeCandidates.size()));
          break;
        default:
          throw new Error("Unreachable");
      }
      int availableIndex = assignableMemoryTypeIndex / 2;
      switch (assignableMemoryTypeIndex % 2) {
        case 0:
          currentAvailableNodes.get(availableIndex).setIn1Node(node);
          break;
        case 1:
          currentAvailableNodes.get(availableIndex).setIn2Node(node);
          break;
        default:
          throw new Error("Unreachable");
      }
      assignableMemoryTypes.set(assignableMemoryTypeIndex, null);
    }
  }
}
