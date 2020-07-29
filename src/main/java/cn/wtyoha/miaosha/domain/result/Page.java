package cn.wtyoha.miaosha.domain.result;


public class Page<T> {
    public int currentPage;
    public int pageSize;
    public int allRecords;
    public int allPages;
    public T data;
    public Page() {

    }

    public Page(int currentPage, int pageSize, int allRecords, int allPages, T data) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.allRecords = allRecords;
        this.allPages = allPages;
        this.data = data;
    }
}
