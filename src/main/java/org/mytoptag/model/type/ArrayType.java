/*
 * Copyright (c) 2018 Stanislav Myachenkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

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


/**
 * Custom data type for postgresql arrays.
 *
 * @param <T> Serializable
 */
public class ArrayType<T extends Serializable> implements UserType {

  private static final int[] SQL_TYPES = {Types.ARRAY};

  private Class<T> typeParameterClass;

  @Override
  public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
    return this.deepCopy(cached);
  }

  @Override
  public Object deepCopy(final Object value) throws HibernateException {
    return value;
  }

  @Override
  public Serializable disassemble(final Object value) throws HibernateException {
    return (T) this.deepCopy(value);
  }

  @Override
  public boolean equals(final Object o1, final Object o2) throws HibernateException {

    if (o1 == null) {
      return o2 == null;
    }
    return o1.equals(o2);
  }

  @Override
  public int hashCode(final Object object) throws HibernateException {
    return object.hashCode();
  }

  @Override
  public Object nullSafeGet(final ResultSet resultSet,
                            final String[] names,
                            final SharedSessionContractImplementor sharedSessionContractImplementor,
                            final Object object) throws HibernateException, SQLException {
    if (resultSet.wasNull()) {
      return null;
    }
    if (resultSet.getArray(names[0]) == null) {
      return new Integer[0];
    }
    final Array array = resultSet.getArray(names[0]);
    final T javaArray = (T) array.getArray();
    return javaArray;
  }

  @Override
  public void nullSafeSet(final PreparedStatement statement,
                          final Object value,
                          final int index,
                          final SharedSessionContractImplementor sharedSessionContractImplementor
  ) throws HibernateException, SQLException {
    final Connection connection = statement.getConnection();
    if (value == null) {
      statement.setNull(index, SQL_TYPES[0]);
    } else {
      final T castObject = (T) value;
      final Array array = connection.createArrayOf("integer", (Object[]) castObject);
      statement.setArray(index, array);
    }
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public Object replace(final Object original,
                        final Object target,
                        final Object owner) throws HibernateException {
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
