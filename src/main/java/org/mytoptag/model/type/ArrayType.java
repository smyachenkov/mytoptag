package org.mytoptag.model.type;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ArrayType<T extends Serializable> implements UserType {

  private static final int[] SQL_TYPES = {Types.ARRAY};

  private Class<T> typeParameterClass;

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return this.deepCopy(cached);
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (T) this.deepCopy(value);
  }

  @Override
  public boolean equals(Object o1, Object o2) throws HibernateException {

    if (o1 == null) {
      return o2 == null;
    }
    return o1.equals(o2);
  }

  @Override
  public int hashCode(Object object) throws HibernateException {
    return object.hashCode();
  }

  @Override
  public Object nullSafeGet(ResultSet resultSet,
                            String[] names,
                            SharedSessionContractImplementor sharedSessionContractImplementor,
                            Object object) throws HibernateException, SQLException {
    if (resultSet.wasNull()) {
      return null;
    }
    if (resultSet.getArray(names[0]) == null) {
      return new Integer[0];
    }

    Array array = resultSet.getArray(names[0]);
    @SuppressWarnings("unchecked")
    T javaArray = (T) array.getArray();
    return javaArray;
  }

  @Override
  public void nullSafeSet(PreparedStatement statement,
                          Object value,
                          int index,
                          SharedSessionContractImplementor sharedSessionContractImplementor
  ) throws HibernateException, SQLException {
    Connection connection = statement.getConnection();
    if (value == null) {
      statement.setNull(index, SQL_TYPES[0]);
    } else {
      @SuppressWarnings("unchecked")
      T castObject = (T) value;
      Array array = connection.createArrayOf("integer", (Object[]) castObject);
      statement.setArray(index, array);
    }
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return original;
  }

  @Override
  public Class<T> returnedClass() {
    return typeParameterClass;
  }

  @Override
  public int[] sqlTypes() {
    return new int[]{Types.ARRAY};
  }
}