package dbZ1;

public class ISAMArtikel implements Comparable<ISAMArtikel>
{

	private int nr;
	private int offset;

	public ISAMArtikel(int nr, int offset)
	{
		this.nr = nr;
		this.offset = offset;
	}

	public int getnr()
		{
			return nr;
		}

	public int getOffset()
		{
			return offset;
		}

	@Override
	public int compareTo(ISAMArtikel o)
		{
			int onr = o.getnr();
			if (this.nr > onr)
				{
					return 1;
				} else if (this.nr == onr)
				{
					return 0;
				} else
				{
					return -1;
				}

		}

}
