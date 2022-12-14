package manager.history;

import tasks.Task;

import java.util.*;

public class CustomLinkedList<T extends Task> {
    class Node<E extends Task> {
        public E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * Указатель на первый элемент списка. Он же first
     */
    private Node<T> head;

    /**
     * Указатель на последний элемент списка. Он же last
     */
    private Node<T> tail;

    /**
     * Размер списка
     */
    private int size = 0;

    Map<Integer, Node<T>> map;

    public CustomLinkedList() {
        map = new HashMap<>();
    }

    public void linkLast(T element) {
        // Если эту задачу уже просматривали, то надо ее сначала удалить
        if (map.containsKey(element.getId())) {
            remove(element.getId());
        }
        if (size == 0) {
            // голова и хвост совпадают
            head = new Node<>(null, element, null);
            tail = head;
            map.put(element.getId(), head);
        } else {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            oldTail.next = newNode;
            tail = newNode;
            map.put(element.getId(), newNode);
        }
        size++;
    }

    public T remove(int id) {
        // Если нет id в мапе
        if (!map.containsKey(id)) {
            return null;
        }
        // Если список пуст
        if (size == 0) {
            return null;
        }
        Node<T> removeNode = map.remove(id);
        removeNode(removeNode);
        return removeNode.data;
    }

    private void removeNode(Node<T> removeNode) {
        // Если в списке 1 элемент
        if (size == 1) {
            head = null;
            tail = null;
            size = 0;
        } else {
            // Если пытаемся удалить голову
            if (head.data.getId() == removeNode.data.getId()) {
                // Головой становится следующий элемент
                head = head.next;
                // У элемента ставшим головой удаляется ссылка на предыдущий элемент
                head.prev = null;
                size--;
            }
            // Если пытаемся удалить хвост
            else if (tail.data.getId() == removeNode.data.getId()) {
                // Хвостом становится предыдущий элемент
                tail = tail.prev;
                // У элемента ставшим хвостом удаляется ссылка на следуюющий элемент
                tail.next = null;
                size--;
            } else {
                // Установка ссылки на следующий элемент для предыдудщего элемента
                removeNode.prev.next = removeNode.next;
                // Установка ссылки на предыдущий элемент для последующего элемента
                removeNode.next.prev = removeNode.prev;
                // Уменьшить размер списка
                size--;
            }
        }
    }


    public List<T> toList() {
        List<T> ans = new ArrayList<>(size);
        if (size > 0) {
            Node<T> currentElement = head;
            while (currentElement != null) {
                ans.add(currentElement.data);
                currentElement = currentElement.next;
            }
        }
        return ans;
    }

    public int size() {
        return size;
    }


}
