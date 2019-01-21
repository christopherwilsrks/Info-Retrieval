package com.DeepIR.LuceneSuggest;

import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ProductIterator implements InputIterator {

    private Iterator<Product> productIterator;
    private Product currentProduct;

    public ProductIterator(Iterator<Product> productIterator) {
        this.productIterator = productIterator;
    }

    @Override
    public long weight() {
        return currentProduct.getNumberSold();
    }

    @Override
    public BytesRef payload() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream    out = new ObjectOutputStream(bos);
            out.writeObject(currentProduct);
            out.close();
            return new BytesRef(bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Well that's unfortunate.");
        }
    }

    @Override
    public boolean hasPayloads() {
        return true;
    }

    @Override
    public Set<BytesRef> contexts() {
        try {
            Set<BytesRef> regions = new HashSet<BytesRef>();
            for (String region : currentProduct.getRegions()) {
                regions.add(new BytesRef(region.getBytes("UTF8")));
            }
            return regions;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Couldn't convert to UTF-8");
        }
    }

    @Override
    public boolean hasContexts() {
        return true;
    }

    @Override
    public BytesRef next() throws IOException {
        if (productIterator.hasNext()) {
            currentProduct = productIterator.next();
            try {
                //返回当前Project的name值，把product类的name属性值作为key
                return new BytesRef(currentProduct.getName().getBytes("UTF8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Couldn't convert to UTF-8",e);
            }
        } else {
            return null;
        }
    }

    @Override
    public Comparator<BytesRef> getComparator() {
        return null;
    }
}
