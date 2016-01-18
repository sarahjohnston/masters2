namespace ApiReviews.Models
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.DataAnnotations;
    using System.ComponentModel.DataAnnotations.Schema;
    using System.Data.Entity.Spatial;

    [Table("sarahjdb.places")]
    public partial class place
    {
        public int Id { get; set; }

        [StringLength(200)]
        public string PlaceName { get; set; }

        public int TotalRatings { get; set; }

        public int NumberReviews { get; set; }
    }
}
