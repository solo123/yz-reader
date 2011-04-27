package com.yazo.util;

import java.io.*;

/**
 * Clase que permite leer ficheros GZIP.
 * 
 * @author Carlos Araiz
 * 
 * @version 1.1.0
 */
public class GZIP
{
	// M醩caras para el flag.
//	private static final int FTEXT_MASK		= 1;
	private static final int FHCRC_MASK		= 2;
	private static final int FEXTRA_MASK	= 4;
	private static final int FNAME_MASK		= 8;
	private static final int FCOMMENT_MASK	= 16;
	// Tipos de bloques.
	private static final int BTYPE_NONE		= 0;
	private static final int BTYPE_FIXED	= 1;
	private static final int BTYPE_DYNAMIC	= 2;
//	private static final int BTYPE_RESERVED	= 3;
	// L韒ites.
	private static final int MAX_BITS			= 16;
	private static final int MAX_CODE_LITERALS	= 287;
	private static final int MAX_CODE_DISTANCES	= 31;
	private static final int MAX_CODE_LENGTHS	= 18;
	private static final int EOB_CODE			= 256;

	/** ********************************************************************** */

	// Variables de lectura de datos comprimidos.
	private static byte buffer[];
	private static int buffer_index,buffer_byte,buffer_bit;
	// Variables de escritura de datos descomprimidos.
	private static byte uncompressed[];
	private static int uncompressed_index;
	// Tablas con datos prefijados.
	private static byte length_extra_bits[]; 	// 257..287
	private static short length_values[];		// 257..287
	private static byte distance_extra_bits[];  // 0..29
	private static short distance_values[];		// 0..29
	private static byte dynamic_length_order[]; // 0..18

	/** ********************************************************************** */
	/** ********************************************************************** */

	/**
	 * Devuelve un stream para leer los datos de un fichero GZIP.
	 * 
	 * @param gzip
	 *            Array con los datos del fichero comprimido
	 * 
	 * @return Stream de lectura
	 */
	public static DataInputStream openDataInputStream(byte gzip[]) throws IOException
	{
		return new DataInputStream(new ByteArrayInputStream(inflate(gzip)));
	}

	/**
	 * Descomprime un fichero GZIP.
	 * 
	 * @param gzip
	 *            Array con los datos del fichero comprimido
	 * 
	 * @return Array con los datos descomprimidos
	 */
	public static byte[] inflate(byte gzip[]) throws IOException
	{
		try
		{
			buffer=gzip;
			// Cabecera.
			if (readBits(16)!=0x8b1f||readBits(8)!=8) throw new IOException("Invalid GZIP format");
			// Flag.
			int flg=readBits(8);
			// Fecha(4) / XFL(1) / OS(1).
			buffer_index+=6;
			// Comprueba los flags.
			if ((flg&FEXTRA_MASK)!=0) buffer_index+=readBits(16);
			if ((flg&FNAME_MASK)!=0) while (buffer[buffer_index++]!=0);
			if ((flg&FCOMMENT_MASK)!=0) while (buffer[buffer_index++]!=0);
			if ((flg&FHCRC_MASK)!=0) buffer_index+=2;
			// Tama駄1�7 de los datos descomprimidos.
			int index=buffer_index;
			buffer_index=buffer.length-4;
			uncompressed=new byte[readBits(16)|(readBits(16)<<16)];
			buffer_index=index;
			// Crea las tablas con datos prefijados.
			length_extra_bits=new byte[]{0,0,0,0,0,0,0,0,1,1,1,1,2,2,2,2,3,3,3,3,4,4,4,4,5,5,5,5,0,99,99};
			length_values=new short[]{3,4,5,6,7,8,9,10,11,13,15,17,19,23,27,31,35,43,51,59,67,83,99,115,131,163,195,227,258,0,0};
			distance_extra_bits=new byte[]{0,0,0,0,1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9,10,10,11,11,12,12,13,13};
			distance_values=new short[]{1,2,3,4,5,7,9,13,17,25,33,49,65,97,129,193,257,385,513,769,1025,1537,2049,3073,4097,6145,8193,12289,16385,24577};
			dynamic_length_order=new byte[]{16,17,18,0,8,7,9,6,10,5,11,4,12,3,13,2,14,1,15};
			// Bloque con datos comprimidos.
			int bfinal=0,btype=0;
			do
			{
				// Lee la cabecera del bloque.
				bfinal=readBits(1);
				btype=readBits(2);
				// Comprueba el tipo de compresi髄1�7.
				if (btype==BTYPE_NONE) inflateStored();
					else if (btype==BTYPE_FIXED) inflateFixed();
					else if (btype==BTYPE_DYNAMIC) inflateDynamic();
					else throw new IOException("Invalid GZIP block");
			}
			while (bfinal==0);
			//
			return uncompressed;
		}
		finally
		{
			buffer_index=buffer_byte=buffer_bit=uncompressed_index=0;
			buffer=uncompressed=length_extra_bits=distance_extra_bits=dynamic_length_order=null;
			length_values=distance_values=null;
		}
	}

	/**
	 * Procesa un bloque sin comprimir.
	 */
	private static void inflateStored()
	{
		// Ignora los bits dentro del byte actual.
		buffer_bit=0;
		// LEN.
		int len=readBits(16);
		// NLEN.
//		int nlen=readBits(16);
		// Lee los datos.
		System.arraycopy(buffer,buffer_index,uncompressed,uncompressed_index,len);
		buffer_index+=len;
		// Actualiza el 韓dice de los datos descomprimidos.
		uncompressed_index+=len;
	}

	/**
	 * Procesa un bloque comprimido con c骴igos fijos.
	 */
	private static void inflateFixed()
	{
		// Genera los 醨boles.
		byte literal_bits[]=new byte[MAX_CODE_LITERALS+1];
		for (int i=0;i<144;i++) literal_bits[i]=8;
		for (int i=144;i<256;i++) literal_bits[i]=9;
		for (int i=256;i<280;i++) literal_bits[i]=7;
		for (int i=280;i<288;i++) literal_bits[i]=8;
		int literal_tree[]=createHuffmanTree(literal_bits,MAX_CODE_LITERALS);
		//
		byte distance_bits[]=new byte[MAX_CODE_DISTANCES+1];
		for (int i=0;i<distance_bits.length;i++) distance_bits[i]=5;
		int distance_tree[]=createHuffmanTree(distance_bits,MAX_CODE_DISTANCES);
		// Descomprime el bloque.
		inflateBlock(literal_tree,distance_tree);
	}

	/**
	 * Procesa un bloque comprimido con c骴igos din醡icos.
	 */
	private static void inflateDynamic()
	{
		// N鷐ero de datos de cada tipo.
		int hlit=readBits(5)+257;
		int hdist=readBits(5)+1;
		int hclen=readBits(4)+4;
		// Lee el n鷐ero de bits para cada c骴igo de longitud.
		byte length_bits[]=new byte[MAX_CODE_LENGTHS+1];
		for (int i=0;i<hclen;i++) length_bits[dynamic_length_order[i]]=(byte)readBits(3);
		// Crea los c骴igos para la longitud.
		int length_tree[]=createHuffmanTree(length_bits,MAX_CODE_LENGTHS);
		// Genera los 醨boles.
		byte literal_bits[]=decodeCodeLengths(length_tree,hlit);
		int literal_tree[]=createHuffmanTree(literal_bits,hlit-1);
		//
		byte distance_bits[]=decodeCodeLengths(length_tree,hdist);
		int distance_tree[]=createHuffmanTree(distance_bits,hdist-1);
		// Descomprime el bloque.
		inflateBlock(literal_tree,distance_tree);
	}

	/**
	 * Procesa un bloque comprimido.
	 */
	private static void inflateBlock(int literal_tree[],int distance_tree[])
	{
		int code=0,leb=0,deb=0;
		while ((code=readCode(literal_tree))!=EOB_CODE)
		{
			if (code>EOB_CODE)
			{
				code=code-257;
				int length=length_values[code];
				if ((leb=length_extra_bits[code])>0) length+=readBits(leb);
				code=readCode(distance_tree);
				int distance=distance_values[code];
				if ((deb=distance_extra_bits[code])>0) distance+=readBits(deb);
				//
				for (int i=0,offset=uncompressed_index-distance;i<length;i++)
					uncompressed[uncompressed_index++]=uncompressed[offset+i];
			}
			else uncompressed[uncompressed_index++]=(byte)code;
		}
	}

	/**
	 * Lee un n鷐ero de bits
	 * 
	 * @param n
	 *            N鷐ero de bits [0..16]
	 */
	private static int readBits(int n)
	{
		// Asegura que tenemos un byte.
		int data=(buffer_bit==0?(buffer_byte=(buffer[buffer_index++]&0xff)):(buffer_byte>>buffer_bit));
		// Lee hasta completar los bits.
		for (int i=(8-buffer_bit);i<n;i+=8)
		{
			buffer_byte=(buffer[buffer_index++]&0xff);
			data|=(buffer_byte<<i);
		}
		// Ajusta la posici髄1�7 actual.
		buffer_bit=(buffer_bit+n)&7;
		// Devuelve el dato.
		return (data&(1<<n)-1);
	}

	/**
	 * Lee un c骴igo.
	 */
	private static int readCode(int tree[])
	{
		int node=tree[0];
		while (node>=0)
		{
			// Lee un byte si es necesario.
			if (buffer_bit==0) buffer_byte=(buffer[buffer_index++]&0xff);
			// Accede al nodo correspondiente.
			node=(((buffer_byte&(1<<buffer_bit))==0)?tree[node>>16]:tree[node&0xffff]);
			// Ajusta la posici髄1�7 actual.
			buffer_bit=(buffer_bit+1)&7;
		}
		return (node&0xffff);
	}

	/**
	 * Crea el 醨bol para los c骴igos Huffman.
	 */
	private static int[] createHuffmanTree(byte bits[],int max_code)
	{
		// N鷐ero de c骴igos por cada longitud de c骴igo.
		int bl_count[]=new int[MAX_BITS+1];
		for (int i=0;i<bits.length;i++) bl_count[bits[i]]++;
		// M韓imo valor num閞ico del c骴igo para cada longitud de c骴igo.
		int code=0;
		bl_count[0]=0;
		int next_code[]=new int[MAX_BITS+1];
		for (int i=1;i<=MAX_BITS;i++) next_code[i]=code=(code+bl_count[i-1])<<1;
		int tree[]=new int[(max_code<<1)+MAX_BITS];
		int tree_insert=1;
		for (int i=0;i<=max_code;i++)
		{
			int len=bits[i];
			if (len!=0)
			{
				code=next_code[len]++;
				// Lo mete en en 醨bol.
				int node=0;
				for (int bit=len-1;bit>=0;bit--)
				{
					int value=code&(1<<bit);
					// Inserta a la izquierda.
					if (value==0)
					{
						int left=tree[node]>>16;
						if (left==0)
						{
							tree[node]|=(tree_insert<<16);
							node=tree_insert++;
						}
						else node=left;
					}
					// Inserta a la derecha.
					else
					{
						int right=tree[node]&0xffff;
						if (right==0)
						{
							tree[node]|=tree_insert;
							node=tree_insert++;
						}
						else node=right;
					}
				}
				// Inserta el c骴igo.
				tree[node]=i|0x80000000;
			}
		}
		return tree;
	}

	/**
	 * Decodifica la longitud de c骴igos (usado en bloques comprimidos con c骴igos
	 * din醡icos).
	 */
	private static byte[] decodeCodeLengths(int length_tree[],int count)
	{
		byte bits[]=new byte[count];
		for (int i=0,code=0,last=0;i<count;)
		{
			code=readCode(length_tree);
			if (code>=16)
			{
				int repeat=0;
				if (code==16)
				{
					repeat=3+readBits(2);
					code=last;
				}
				else
				{
					if (code==17) repeat=3+readBits(3);
						else repeat=11+readBits(7);
					code=0;
				}
				while (repeat-->0) bits[i++]=(byte)code;
			}
			else if (code!=0) bits[i++]=(byte)code;
			else i++;
			//
			last=code;
		}
		return bits;
	}
}