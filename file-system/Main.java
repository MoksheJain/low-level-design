import java.util.*;

abstract class FileSystemNode {
    protected String name;

    public FileSystemNode(String name) {
        this.name = name;
    }
    
    public abstract int getSize();
    public abstract void ls(String indent);
    public String getName() {
        return name;
    }
}

class File extends FileSystemNode {
    private int size;

    public File(String name, int size) {
        super(name);
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void ls(String indent) {
        System.out.println(indent + name);
    }
}

class Directory extends FileSystemNode {
    private List<FileSystemNode> children;

    public Directory(String name) {
        super(name);
        children = new ArrayList<>();
    }

    public void add(FileSystemNode node) {
        children.add(node);
    }

    public void remove(String name) {
        children.removeIf(node->node.getName().equals(name));
    }

    public List<FileSystemNode> getChildren() {
        return children;
    }

    @Override
    public int getSize() {
        int total = 0;
        
        for(FileSystemNode node: children) {
            total += node.getSize();
        }

        return total;
    }

    @Override
    public void ls(String indent) {
        System.out.println(indent + "[" + name + "]");

        for(FileSystemNode node: children) {
            node.ls(indent + "  ");
        }
    }
}

class FileSystem {
    private Directory root;

    public FileSystem() {
        root = new Directory("root");
    }

    public Directory getRoot() {
        return root;
    }

    public void display() {
        root.ls("");
    }
}

public class Main {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();

        Directory docs = new Directory("Documents");
        Directory photos = new Directory("Photos");
        Directory importantDocs = new Directory("Important Docs");

        docs.add(new File("resume.pdf", 100));
        docs.add(new File("movies.txt", 100));
        docs.add(importantDocs);

        importantDocs.add(new File("doc1.txt", 200));

        photos.add(new File("trip.jpg", 100));

        fs.getRoot().add(docs);
        fs.getRoot().add(photos);
        fs.getRoot().add(new File("music.mp3", 43));

        fs.display();

        System.out.println("\nTotal size: " + fs.getRoot().getSize() + " KB");
    }
}