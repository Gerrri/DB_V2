package dbZ1;

public class ISAMArtikel implements Comparable<ISAMArtikel>
{

	private int nr;
	private long offset;

	public ISAMArtikel(int nr, long offset)
	{
		this.nr = nr;
		this.offset = offset;
	}

	public int getnr()
		{
			return nr;
		}

	public long getOffset()
		{
			return offset;
		}

	@Override
	public int compareTo(ISAMArtikel in)
		{
			int onr = in.getnr();
			if (onr < this.nr)
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
