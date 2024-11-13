package xin.manong.weapon.base.collection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.KryoBufferUnderflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * dump文件数据读取器
 *
 * @author frankcl
 * @date 2023-04-27 14:43:48
 */
class DumpReader<T> implements RecordReader<T> {

    private static final Logger logger = LoggerFactory.getLogger(DumpReader.class);

    private final String dumpFile;
    private final Class<T> recordClass;
    private final Kryo kryo;
    private Input input;
    private T record;

    public DumpReader(String dumpFile, Class<T> recordClass, Kryo kryo) {
        this.dumpFile = dumpFile;
        this.recordClass = recordClass;
        this.kryo = kryo;
    }

    @Override
    public boolean open() {
        if (input != null) {
            logger.warn("dump reader has been opened for file[{}]", dumpFile);
            return false;
        }
        try {
            input = new Input(new FileInputStream(dumpFile));
            logger.info("open dump reader success for file[{}]", dumpFile);
            return true;
        } catch (IOException e) {
            logger.error("open dump reader failed for file[{}]", dumpFile);
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void close() {
        if (input == null) return;
        input.close();
        logger.info("close dump reader success for file[{}]", dumpFile);
    }

    @Override
    public T read() {
        try {
            return record = kryo.readObject(input, recordClass);
        } catch (KryoBufferUnderflowException e) {
            return record = null;
        }
    }

    @Override
    public T peak() {
        return record;
    }
}
