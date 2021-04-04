import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class HW1 {
	
	public static void main(String[] args) {
		Tree23<String, Integer> st = new Tree23<>();
		Scanner sc = new Scanner(System.in);	
		System.out.print("입력 파일 이름? ");
		String fname = sc.nextLine();	// 파일 이름을 입력
		System.out.print("난수 생성을 위한 seed 값? ");
		Random rand = new Random(sc.nextLong());
		sc.close();
		try {
			sc = new Scanner(new File(fname));
			long start = System.currentTimeMillis();
			while (sc.hasNext()) {
				String word = sc.next();
				if (!st.contains(word))
					st.put(word, 1);
				else	st.put(word, st.get(word) + 1);
			}
			long end = System.currentTimeMillis();
			System.out.println("입력 완료: 소요 시간 = " + (end-start) + "ms");
			
			System.out.println("### 생성 시점의 트리 정보");
			print_tree(st);		// 정상적으로 출력되면 50점
			
			ArrayList<String> keyList = (ArrayList<String>) st.keys();
			Collections.shuffle(keyList, rand);
			int loopCount = (int)(keyList.size() * 0.95);
			for (int i = 0; i < loopCount; i++) {
				st.delete(keyList.get(i));						// 주석 처리 가능
			}
			System.out.println("\n### 키 삭제 후 트리 정보");			// 주석 처리 가능
			print_tree(st);										// 주석 처리 가능. 여기까지 정상적으로 출력되면 100점
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		if (sc != null)
			sc.close();
	}
	
	private static void print_tree(Tree23<String, Integer> st) {
		System.out.println("등록된 단어 수 = " + st.size());		
		System.out.println("트리의 깊이 = " + st.depth());
		
		String maxKey = "";
		int maxValue = 0;
		for (String word : st.keys())
			if (st.get(word) > maxValue) {
				maxValue = st.get(word);
				maxKey = word;
			}
		System.out.println("가장 빈번히 나타난 단어와 빈도수: " + maxKey + " " + maxValue);
	}
}

// 여기서 부터 Tree23 클래스를 정의하는 프로그램 추가할 것!
class Tree23<K extends Comparable<K>, V> {
	private K key1 = null, key2 = null;
	private V value1 = null, value2 = null;
	private int N = 0;

	private Tree23<K, V> left = null, middle = null, right = null;
	private Tree23<K, V> parent = null;
	private Tree23<K, V> root = null;

	public void put(K key, V value) {
		Tree23<K, V> tempNode = find_node(key);
		if (root == null) {
			key1 = key;
			value1 = value;
			root = this;
			N++;
		} else {
			if (tempNode.key2 == null) {
				if (key.compareTo(tempNode.key1) < 0) {
					tempNode.key2 = tempNode.key1;
					tempNode.value2 = tempNode.value1;
					tempNode.key1 = key;
					tempNode.value1 = value;
					N++;
				} else if (key.compareTo(tempNode.key1) > 0) {
					tempNode.key2 = key;
					tempNode.value2 = value;
					N++;
				} else {
					tempNode.value1 = value;
				}
			}
			else {
				if (key.equals(tempNode.key1)) {
					tempNode.value1 = value;
				} else if(key.equals(tempNode.key2)) {
					tempNode.value2 = value;
				} else {
					Tree23<K, V> newNode = new Tree23<>();
					newNode.key1 = key;
					newNode.value1 = value;
					combine(tempNode, newNode);
					N++;
				}
			}
		}
	}

	public Tree23<K, V> find_node(K key) {
		if (left == null)
			return this;
		if (key2 == null) {
			if (key.compareTo(key1) < 0)
				return left.find_node(key);
			else if (key.compareTo(key1) > 0)
				return middle.find_node(key);
			else
				return this;
		} else {
			if (key.compareTo(key1) < 0)
				return left.find_node(key);
			else if (key.compareTo(key1) > 0 && key.compareTo(key2) < 0)
				return middle.find_node(key);
			else if (key.compareTo(key2) > 0)
				return right.find_node(key);
			else
				return this;
		}
	}

	public Boolean contains(K key) {
		Tree23<K, V> tempNode = find_node(key);
		if (tempNode.key2 == null) {
			return key.equals(tempNode.key1);
		} else {
			return key.equals(tempNode.key1) || key.equals(tempNode.key2);
		}
	}

	public V get(K key) {
		Tree23<K, V> tempNode = find_node(key);
		if (tempNode.key2 == null) {
			if (key.equals(tempNode.key1))
				return tempNode.value1;
			else
				return null;
		} else {
			if (key.equals(tempNode.key1))
				return tempNode.value1;
			else if (key.equals(tempNode.key2))
				return tempNode.value2;
			else
				return null;
		}
	}

	public Boolean isEmpty() {
		return root != null;
	}

	public int size() {
		return N;
	}

	public Iterable<K> keys() {
		ArrayList<K> list = new ArrayList<>();
		inorder(list);
		return list;
	}

	public void inorder(ArrayList<K> list) {
		if (left != null)
			left.inorder(list);
		list.add(key1);
		if (middle != null)
			middle.inorder(list);
		if (key2 != null)
			list.add(key2);
		if (right != null)
			right.inorder(list);
	}

	public int depth() {
		int D = 0;
		Tree23<K, V> tempNode = this;
		while (tempNode != null) {
			tempNode = tempNode.left;
			D++;
		}
		return D;
	}

	public void combine(Tree23<K, V> node1, Tree23<K, V> node2) {
		if (node1.key2 == null) {
			if (node2.key1.compareTo(node1.key1) < 0) {
				node1.key2 = node1.key1;
				node1.value2 = node1.value1;
				node1.right = node1.middle;
				node1.key1 = node2.key1;
				node1.left = node2.left;
				node1.middle = node2.middle;
				node2.left.parent = node1;
				node2.middle.parent = node1;
			} else {
				node1.key2 = node2.key1;
				node1.value2 = node2.value1;
				node1.middle = node2.left;
				node1.right = node2.middle;
				node2.left.parent = node1;
				node2.middle.parent = node1;
			}
		} else {
			if (node2.key1.compareTo(node1.key1) < 0) {
				Tree23<K, V> parentNode = node1.parent;
				Tree23<K, V> newNode = new Tree23<>();
				newNode.key1 = node1.key2;
				newNode.value1 = node1.value2;
				node1.key2 = null;
				node1.value2 = null;

				if (node1.middle != null) {
					newNode.left = node1.middle;
					node1.middle.parent = newNode;
				}
				if (node1.right != null) {
					newNode.middle = node1.right;
					node1.right.parent = newNode;
				}

				node1.left = node2;
				node2.parent = node1;
				node1.middle = newNode;
				newNode.parent = node1;
				node1.right = null;
				node2.root = node1.root;
				newNode.root = node1.root;
				if (parentNode != null)
					combine(parentNode, node1);
			} else if (node2.key1.compareTo(node1.key1) > 0 && node2.key1.compareTo(node1.key2) < 0) { // o
				Tree23<K, V> parentNode = node1.parent;
				Tree23<K, V> newNode = new Tree23<>();
				newNode.key1 = node1.key2;
				newNode.value1 = node1.value2;
				node1.key2 = node2.key1;
				node1.value2 = node2.value1;
				node2.key1 = node1.key1;
				node2.value1 = node1.value1;
				node1.key1 = node1.key2;
				node1.value1 = node1.value2;
				node1.key2 = null;
				node1.value2 = null;

				if (node2.middle != null) {
					newNode.left = node2.middle;
					node2.middle.parent = newNode;
				}
				if (node1.right != null) {
					newNode.middle = node1.right;
					node1.right.parent = newNode;
				}
				if (node2.left != null) {
					node2.middle = node2.left;
					node2.left.parent = node2;
				}
				if (node1.left != null) {
					node2.left = node1.left;
					node1.left.parent = node2;
				}

				node1.left = node2;
				node1.middle = newNode;
				node1.right = null;
				node2.parent = node1;
				newNode.parent = node1;
				node2.root = node1.root;
				newNode.root = node1.root;
				if (parentNode != null)
					combine(parentNode, node1);
			} else {
				Tree23<K, V> parentNode = node1.parent;
				Tree23<K, V> newNode = new Tree23<>();
				newNode.key1 = node1.key1;
				newNode.value1 = node1.value1;
				node1.key1 = node1.key2;
				node1.value1 = node1.value2;
				node1.key2 = null;
				node1.value2 = null;

				if (node1.left != null) {
					newNode.left = node1.left;
					node1.left.parent = newNode;
				}
				if (node1.middle != null) {
					newNode.middle = node1.middle;
					node1.middle.parent = newNode;
				}

				node1.left = newNode;
				node1.middle = node2;
				node1.right = null;
				newNode.parent = node1;
				node2.parent = node1;
				node2.root = node1.root;
				newNode.root = node1.root;
				if (parentNode != null)
					combine(parentNode, node1);
			}
		}
	}

	public void delete(K key) {
		Tree23<K, V> tempNode = find_node(key);
		Tree23<K, V> successorNode;
		if (tempNode.left != null) {
			if (key.equals(tempNode.key1)) {
				successorNode = tempNode.middle;
				while (successorNode.left != null)
					successorNode = successorNode.left;
				tempNode.key1 = successorNode.key1;
				tempNode.value1 = successorNode.value1;
				successorNode.key1 = null;
				successorNode.value1 = null;
			} else {
				successorNode = tempNode.right;
				while (successorNode.left != null)
					successorNode = successorNode.left;
				tempNode.key2 = successorNode.key1;
				tempNode.value2 = successorNode.value1;
				successorNode.key1 = null;
				successorNode.value1 = null;
			}
			deleteProcess(successorNode);
		} else {
			if (key.equals(tempNode.key1)) {
				if (tempNode.key2 != null) {
					tempNode.key1 = tempNode.key2;
					tempNode.value1 = tempNode.value2;
					tempNode.key2 = null;
					tempNode.value2 = null;
				} else {
					tempNode.key1 = null;
					tempNode.value1 = null;
					deleteProcess(tempNode);
				}
			} else {
				tempNode.key2 = null;
				tempNode.value2 = null;
			}
		}
		N--;
	}

	public void deleteProcess(Tree23<K, V> tempNode) {
		Tree23<K, V> parentNode = tempNode.parent;
		if (tempNode.key1 != null) {

		} else if (tempNode.key2 != null) {
			tempNode.key1 = tempNode.key2;
			tempNode.value1 = tempNode.value2;
			tempNode.key2 = null;
			tempNode.value2 = null;
		} else {
			if (tempNode == parentNode.left) {
				if (parentNode.middle.key2 != null) {
					tempNode.key1 = parentNode.key1;
					tempNode.value1 = parentNode.value1;
					parentNode.key1 = parentNode.middle.key1;
					parentNode.value1 = parentNode.middle.value1;
					parentNode.middle.key1 = parentNode.middle.key2;
					parentNode.middle.value1 = parentNode.middle.value2;
					parentNode.middle.key2 = null;
					parentNode.middle.value2 = null;
					if (tempNode.middle != null)
						tempNode.left = tempNode.middle;

					if (parentNode.middle.left != null) {
						tempNode.middle = parentNode.middle.left;
						tempNode.middle.parent = tempNode;
					}
					parentNode.middle.left = parentNode.middle.middle;
					parentNode.middle.middle = parentNode.middle.right;
					parentNode.middle.right = null;
				} else {
					if (parentNode.key2 != null) {
						parentNode.middle.key2 = parentNode.middle.key1;
						parentNode.middle.value2 = parentNode.middle.value1;
						parentNode.middle.key1 = parentNode.key1;
						parentNode.middle.value1 = parentNode.value1;
						parentNode.key1 = parentNode.key2;
						parentNode.value1 = parentNode.value2;
						parentNode.key2 = null;
						parentNode.value2 = null;
						parentNode.left = parentNode.middle;
						parentNode.middle = parentNode.right;
						parentNode.right = null;
						parentNode.left.right = parentNode.left.middle;
						parentNode.left.middle = parentNode.left.left;
						if (tempNode.left != null) {
							parentNode.left.left = tempNode.left;
							tempNode.left.parent = parentNode.left;
						} else if (tempNode.middle != null) {
							parentNode.left.left = tempNode.middle;
							tempNode.middle.parent = parentNode.left;
						} else {
							parentNode.left.left = null;
						}
					} else {
						parentNode.middle.key2 = parentNode.middle.key1;
						parentNode.middle.value2 = parentNode.middle.value1;
						parentNode.middle.key1 = parentNode.key1;
						parentNode.middle.value1 = parentNode.value1;
						parentNode.key1 = null;
						parentNode.value1 = null;
						parentNode.middle.right = parentNode.middle.middle;
						parentNode.middle.middle = parentNode.middle.left;
						parentNode.left = null;
						if (tempNode.left != null) {
							parentNode.middle.left = tempNode.left;
							tempNode.left.parent = parentNode.middle;
						} else if (tempNode.middle != null) {
							parentNode.middle.left = tempNode.middle;
							tempNode.middle.parent = parentNode.middle;
						} else {
							parentNode.middle.left = null;
						}
						if (parentNode.parent != null) {
							deleteProcess(parentNode);
						} else {
							parentNode.key1 = parentNode.middle.key1;
							parentNode.value1 = parentNode.middle.value1;
							parentNode.key2 = parentNode.middle.key2;
							parentNode.value2 = parentNode.middle.value2;
							parentNode.right = parentNode.middle.right;
							parentNode.left = parentNode.middle.left;
							parentNode.middle = parentNode.middle.middle;
							if (parentNode.left != null)
								parentNode.left.parent = parentNode;
							if (parentNode.middle != null)
								parentNode.middle.parent = parentNode;
							if (parentNode.right != null)
								parentNode.right.parent = parentNode;
							root = parentNode;
						}
					}
				}
			} else if(tempNode == parentNode.middle) {
				if (parentNode.left.key2 != null) {
					tempNode.key1 = parentNode.key1;
					tempNode.value1 = parentNode.value1;
					parentNode.key1 = parentNode.left.key2;
					parentNode.value1 = parentNode.left.value2;
					parentNode.left.key2 = null;
					parentNode.left.value2 = null;
					if (tempNode.left != null)
						tempNode.middle = tempNode.left;
					if (parentNode.left.right != null) {
						tempNode.left = parentNode.left.right;
						tempNode.left.parent = tempNode;
					}
					parentNode.left.right = null;
				} else if (parentNode.right != null && parentNode.right.key2 != null) {
					tempNode.key1 = parentNode.key2;
					tempNode.value1 = parentNode.value2;
					parentNode.key2 = parentNode.right.key1;
					parentNode.value2 = parentNode.right.value1;
					parentNode.right.key1 = parentNode.right.key2;
					parentNode.right.value1 = parentNode.right.value2;
					parentNode.right.key2 = null;
					parentNode.right.value2 = null;
					if (tempNode.middle != null)
						tempNode.left = tempNode.middle;
					if (parentNode.right.left != null) {
						tempNode.middle = parentNode.right.left;
						tempNode.middle.parent = tempNode;
					}
					parentNode.right.left = parentNode.right.middle;
					parentNode.right.middle = parentNode.right.right;
					parentNode.right.right = null;
				} else {
					if (parentNode.key2 != null) {
						parentNode.left.key2 = parentNode.key1;
						parentNode.left.value2 = parentNode.value1;
						parentNode.key1 = parentNode.key2;
						parentNode.value1 = parentNode.value2;
						parentNode.key2 = null;
						parentNode.value2 = null;
						parentNode.middle = parentNode.right;
						parentNode.right = null;
						if (tempNode.left != null) {
							parentNode.left.right = tempNode.left;
							tempNode.left.parent = parentNode.left;
						} else if (tempNode.middle != null) {
							parentNode.left.right = tempNode.middle;
							tempNode.middle.parent = parentNode.left;
						} else {
							parentNode.left.right = null;
						}
					} else {
						parentNode.left.key2 = parentNode.key1;
						parentNode.left.value2 = parentNode.value1;
						parentNode.key1 = null;
						parentNode.value1 = null;
						parentNode.middle = null;
						if (tempNode.left != null) {
							parentNode.left.right = tempNode.left;
							tempNode.left.parent = parentNode.left;
						} else if (tempNode.middle != null) {
							parentNode.left.right = tempNode.middle;
							tempNode.middle.parent = parentNode.left;
						} else {
							parentNode.left.right = null;
						}
						if (parentNode.parent != null) {
							deleteProcess(parentNode);
						} else {
							parentNode.key1 = parentNode.left.key1;
							parentNode.value1 = parentNode.left.value1;
							parentNode.key2 = parentNode.left.key2;
							parentNode.value2 = parentNode.left.value2;
							parentNode.right = parentNode.left.right;
							parentNode.middle = parentNode.left.middle;
							parentNode.left = parentNode.left.left;
							if (parentNode.left != null)
								parentNode.left.parent = parentNode;
							if (parentNode.middle != null)
								parentNode.middle.parent = parentNode;
							if (parentNode.right != null)
								parentNode.right.parent = parentNode;
							root = parentNode;
						}
					}
				}
			} else {
				if (parentNode.middle.key2 != null) {
					tempNode.key1 = parentNode.key2;
					tempNode.value1 = parentNode.value2;
					parentNode.key2 = parentNode.middle.key2;
					parentNode.value2 = parentNode.middle.value2;
					parentNode.middle.key2 = null;
					parentNode.middle.value2 = null;
					if (tempNode.left != null)
						tempNode.middle = tempNode.left;

					if (parentNode.middle.right != null) {
						tempNode.left = parentNode.middle.right;
						tempNode.left.parent = tempNode;
					}

					parentNode.middle.right = null;
				} else {
					parentNode.middle.key2 = parentNode.key2;
					parentNode.middle.value2 = parentNode.value2;
					parentNode.key2 = null;
					parentNode.value2 = null;
					parentNode.right = null;
					if (tempNode.left != null) {
						parentNode.middle.right = tempNode.left;
						tempNode.left.parent = parentNode.middle;
					} else if (tempNode.middle != null) {
						parentNode.middle.right = tempNode.middle;
						tempNode.middle.parent = parentNode.middle;
					} else {
						parentNode.middle.right = null;
					}
				}
			}
		}
	}
}

