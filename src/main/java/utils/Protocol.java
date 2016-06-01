// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: protocol.proto

package utils;

public final class Protocol {
  private Protocol() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface ArrayProtoOrBuilder extends
      // @@protoc_insertion_point(interface_extends:ArrayProto)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated int32 element = 1;</code>
     */
    java.util.List<java.lang.Integer> getElementList();
    /**
     * <code>repeated int32 element = 1;</code>
     */
    int getElementCount();
    /**
     * <code>repeated int32 element = 1;</code>
     */
    int getElement(int index);
  }
  /**
   * Protobuf type {@code ArrayProto}
   */
  public  static final class ArrayProto extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:ArrayProto)
      ArrayProtoOrBuilder {
    // Use ArrayProto.newBuilder() to construct.
    private ArrayProto(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
    }
    private ArrayProto() {
      element_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private ArrayProto(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry) {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                element_ = new java.util.ArrayList<java.lang.Integer>();
                mutable_bitField0_ |= 0x00000001;
              }
              element_.add(input.readInt32());
              break;
            }
            case 10: {
              int length = input.readRawVarint32();
              int limit = input.pushLimit(length);
              if (!((mutable_bitField0_ & 0x00000001) == 0x00000001) && input.getBytesUntilLimit() > 0) {
                element_ = new java.util.ArrayList<java.lang.Integer>();
                mutable_bitField0_ |= 0x00000001;
              }
              while (input.getBytesUntilLimit() > 0) {
                element_.add(input.readInt32());
              }
              input.popLimit(limit);
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw new RuntimeException(e.setUnfinishedMessage(this));
      } catch (java.io.IOException e) {
        throw new RuntimeException(
            new com.google.protobuf.InvalidProtocolBufferException(
                e.getMessage()).setUnfinishedMessage(this));
      } finally {
        if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
          element_ = java.util.Collections.unmodifiableList(element_);
        }
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return utils.Protocol.internal_static_ArrayProto_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return utils.Protocol.internal_static_ArrayProto_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              utils.Protocol.ArrayProto.class, utils.Protocol.ArrayProto.Builder.class);
    }

    public static final int ELEMENT_FIELD_NUMBER = 1;
    private java.util.List<java.lang.Integer> element_;
    /**
     * <code>repeated int32 element = 1;</code>
     */
    public java.util.List<java.lang.Integer>
        getElementList() {
      return element_;
    }
    /**
     * <code>repeated int32 element = 1;</code>
     */
    public int getElementCount() {
      return element_.size();
    }
    /**
     * <code>repeated int32 element = 1;</code>
     */
    public int getElement(int index) {
      return element_.get(index);
    }
    private int elementMemoizedSerializedSize = -1;

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (getElementList().size() > 0) {
        output.writeRawVarint32(10);
        output.writeRawVarint32(elementMemoizedSerializedSize);
      }
      for (int i = 0; i < element_.size(); i++) {
        output.writeInt32NoTag(element_.get(i));
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      {
        int dataSize = 0;
        for (int i = 0; i < element_.size(); i++) {
          dataSize += com.google.protobuf.CodedOutputStream
            .computeInt32SizeNoTag(element_.get(i));
        }
        size += dataSize;
        if (!getElementList().isEmpty()) {
          size += 1;
          size += com.google.protobuf.CodedOutputStream
              .computeInt32SizeNoTag(dataSize);
        }
        elementMemoizedSerializedSize = dataSize;
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    public static utils.Protocol.ArrayProto parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static utils.Protocol.ArrayProto parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static utils.Protocol.ArrayProto parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static utils.Protocol.ArrayProto parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static utils.Protocol.ArrayProto parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static utils.Protocol.ArrayProto parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static utils.Protocol.ArrayProto parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static utils.Protocol.ArrayProto parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static utils.Protocol.ArrayProto parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static utils.Protocol.ArrayProto parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(utils.Protocol.ArrayProto prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code ArrayProto}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:ArrayProto)
        utils.Protocol.ArrayProtoOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return utils.Protocol.internal_static_ArrayProto_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return utils.Protocol.internal_static_ArrayProto_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                utils.Protocol.ArrayProto.class, utils.Protocol.ArrayProto.Builder.class);
      }

      // Construct using utils.Protocol.ArrayProto.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        element_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return utils.Protocol.internal_static_ArrayProto_descriptor;
      }

      public utils.Protocol.ArrayProto getDefaultInstanceForType() {
        return utils.Protocol.ArrayProto.getDefaultInstance();
      }

      public utils.Protocol.ArrayProto build() {
        utils.Protocol.ArrayProto result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public utils.Protocol.ArrayProto buildPartial() {
        utils.Protocol.ArrayProto result = new utils.Protocol.ArrayProto(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) == 0x00000001)) {
          element_ = java.util.Collections.unmodifiableList(element_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.element_ = element_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof utils.Protocol.ArrayProto) {
          return mergeFrom((utils.Protocol.ArrayProto)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(utils.Protocol.ArrayProto other) {
        if (other == utils.Protocol.ArrayProto.getDefaultInstance()) return this;
        if (!other.element_.isEmpty()) {
          if (element_.isEmpty()) {
            element_ = other.element_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureElementIsMutable();
            element_.addAll(other.element_);
          }
          onChanged();
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        utils.Protocol.ArrayProto parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (utils.Protocol.ArrayProto) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.util.List<java.lang.Integer> element_ = java.util.Collections.emptyList();
      private void ensureElementIsMutable() {
        if (!((bitField0_ & 0x00000001) == 0x00000001)) {
          element_ = new java.util.ArrayList<java.lang.Integer>(element_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated int32 element = 1;</code>
       */
      public java.util.List<java.lang.Integer>
          getElementList() {
        return java.util.Collections.unmodifiableList(element_);
      }
      /**
       * <code>repeated int32 element = 1;</code>
       */
      public int getElementCount() {
        return element_.size();
      }
      /**
       * <code>repeated int32 element = 1;</code>
       */
      public int getElement(int index) {
        return element_.get(index);
      }
      /**
       * <code>repeated int32 element = 1;</code>
       */
      public Builder setElement(
          int index, int value) {
        ensureElementIsMutable();
        element_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated int32 element = 1;</code>
       */
      public Builder addElement(int value) {
        ensureElementIsMutable();
        element_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated int32 element = 1;</code>
       */
      public Builder addAllElement(
          java.lang.Iterable<? extends java.lang.Integer> values) {
        ensureElementIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, element_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated int32 element = 1;</code>
       */
      public Builder clearElement() {
        element_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:ArrayProto)
    }

    // @@protoc_insertion_point(class_scope:ArrayProto)
    private static final utils.Protocol.ArrayProto DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new utils.Protocol.ArrayProto();
    }

    public static utils.Protocol.ArrayProto getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ArrayProto>
        PARSER = new com.google.protobuf.AbstractParser<ArrayProto>() {
      public ArrayProto parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        try {
          return new ArrayProto(input, extensionRegistry);
        } catch (RuntimeException e) {
          if (e.getCause() instanceof
              com.google.protobuf.InvalidProtocolBufferException) {
            throw (com.google.protobuf.InvalidProtocolBufferException)
                e.getCause();
          }
          throw e;
        }
      }
    };

    public static com.google.protobuf.Parser<ArrayProto> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ArrayProto> getParserForType() {
      return PARSER;
    }

    public utils.Protocol.ArrayProto getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_ArrayProto_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_ArrayProto_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016protocol.proto\"\035\n\nArrayProto\022\017\n\007elemen" +
      "t\030\001 \003(\005B\007\n\005utilsb\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_ArrayProto_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_ArrayProto_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_ArrayProto_descriptor,
        new java.lang.String[] { "Element", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}